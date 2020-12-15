package com.wafka.thread.impl;

import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.MethodResult;
import com.wafka.thread.AbstractConsumerThread;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.types.OperationStatus;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerThreadImpl extends AbstractConsumerThread {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThreadImpl.class);
	
	private static final MethodResult noOpMethodResult= 
			new MethodResult("", OperationStatus.NOOP);

	private final AtomicBoolean shouldStopPolling;
	private final AtomicBoolean shouldCommitOffset;
	private final AtomicBoolean shouldUpdateSubscriptions;
	private final AtomicBoolean shouldUnsubscribe;
	private final AtomicBoolean shouldGetSubscriptionList;

	private final Semaphore wakeUpSemaphore;

	private final ConsumerThreadSettings consumerThreadSettings;
	private final KafkaConsumer<String, byte[]> kafkaConsumer;

	private final Collection<String> activeTopics;

	private final Map<String, CompletableFuture<Object>> asyncMethodResponsesMap;

	private interface DoNotDisturbeOperation<T> {
		T perform();
	}

	public ConsumerThreadImpl(ConsumerThreadSettings consumerThreadSettings) {
		setName("WSC-Thread-" + consumerThreadSettings.getiConsumerIdentifier());

		this.consumerThreadSettings = consumerThreadSettings;
		this.kafkaConsumer = consumerThreadSettings.getWrappedConsumer();

		activeTopics = new HashSet<>();
		shouldStopPolling = new AtomicBoolean(false);
		shouldCommitOffset = new AtomicBoolean(false);
		shouldUpdateSubscriptions = new AtomicBoolean(false);
		shouldUnsubscribe = new AtomicBoolean(false);
		shouldGetSubscriptionList = new AtomicBoolean(false);

		wakeUpSemaphore = new Semaphore(1);

		asyncMethodResponsesMap = new HashMap<>();
	}

	@Override
	public void run() {
		IConsumerThreadCallback iWebSocketConsumerCallback = consumerThreadSettings.getiWebSocketConsumerCallback();
		Duration pollLoopDuration = consumerThreadSettings.getPollLoopDuration();

		while (!shouldStopPolling.get()) {
			try {
				ConsumerRecords<String, byte[]> consumerRecords = kafkaConsumer.poll(pollLoopDuration);
				if (!consumerRecords.isEmpty()) {
					iWebSocketConsumerCallback.onRecordsReceived(consumerRecords);
				}

			} catch (WakeupException exception) {
				MethodResult methodResult = handleWakeupInterrupt();
				CompletableFuture<Object> future = asyncMethodResponsesMap.get(methodResult.getMethodName());
				// TODO: avoid this if putting all the method names inside asyncMethodResponseMap
				if (future != null) {
					future.complete(methodResult.getResult());
				}

			} catch (Exception exception) {
				iWebSocketConsumerCallback.onConsumerError(exception);
			}
		}
	}

	@Override
	public void commitSync() {
		doNotDisturbeOperaton(() -> {
			shouldCommitOffset.set(true);
			kafkaConsumer.wakeup();
			return OperationStatus.NOOP;
		});
	}

	@Override
	public void stopPolling() {
		doNotDisturbeOperaton(() -> {
			shouldStopPolling.set(true);
			kafkaConsumer.wakeup();
			return OperationStatus.NOOP;
		});
	}

	@Override
	public void updateSubscriptions(Collection<String> topics) {
		doNotDisturbeOperaton(() -> {
			activeTopics.addAll(topics);
			shouldUpdateSubscriptions.set(true);
			kafkaConsumer.wakeup();
			return OperationStatus.NOOP;
		});
	}

	@Override
	public void unsubscribe() {
		doNotDisturbeOperaton(() -> {
			activeTopics.clear();
			shouldUnsubscribe.set(true);
			kafkaConsumer.wakeup();
			return OperationStatus.NOOP;
		});
	}

	@Override
	public Future<Object> getSubscriptions() {
		asyncMethodResponsesMap.put("getSubscriptions", new CompletableFuture<>());

		return doNotDisturbeOperaton(() -> {
			shouldGetSubscriptionList.set(true);
			kafkaConsumer.wakeup();
			return asyncMethodResponsesMap.get("getSubscriptions");

		}).orElseGet(() -> CompletableFuture.completedFuture(Collections.emptySet()));
	}

	private MethodResult handleWakeupInterrupt() {
		return doNotDisturbeOperaton(() -> {
			if (shouldStopPolling.get()) {
				kafkaConsumer.commitSync();
				return new MethodResult("stopPolling", OperationStatus.NOOP);

			} else if (shouldCommitOffset.get()) {
				kafkaConsumer.commitSync();
				shouldCommitOffset.set(false);
				return new MethodResult("commitAsync", OperationStatus.NOOP);

			} else if (shouldUpdateSubscriptions.get()) {
				kafkaConsumer.commitSync();
				kafkaConsumer.subscribe(activeTopics);
				shouldUpdateSubscriptions.set(false);
				return new MethodResult("udateSubscriptions", OperationStatus.NOOP);

			} else if (shouldUnsubscribe.get()) {
				kafkaConsumer.commitSync();
				kafkaConsumer.unsubscribe();
				shouldUnsubscribe.set(false);
				return new MethodResult("unsubscribe", OperationStatus.NOOP);

			} else if (shouldGetSubscriptionList.get()) {
				kafkaConsumer.commitSync();
				Set<String> subscriptions = kafkaConsumer.subscription();
				shouldGetSubscriptionList.set(false);
				return new MethodResult("getSubscriptions", subscriptions);
			}
			return noOpMethodResult;

		}).orElse(noOpMethodResult);
	}

	private <T> Optional<T> doNotDisturbeOperaton(DoNotDisturbeOperation<T> doNotDisturbeOperation) {
		try {
			wakeUpSemaphore.acquireUninterruptibly();
			return Optional.of(doNotDisturbeOperation.perform());

		} catch (Exception exception) {
			LOGGER.error("An error occurred inside thread {}: {}", getName(), exception.getMessage());
			return Optional.empty();

		} finally {
			wakeUpSemaphore.release();
		}
	}
}
