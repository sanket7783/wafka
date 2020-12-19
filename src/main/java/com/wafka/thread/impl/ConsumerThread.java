package com.wafka.thread.impl;

import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.ConsumerId;
import com.wafka.model.MethodResult;
import com.wafka.thread.AbstractConsumerThread;
import com.wafka.thread.IConsumerThreadCallback;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerThread extends AbstractConsumerThread {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThread.class);

	private final AtomicBoolean shouldStopPolling;
	private final AtomicBoolean shouldCommitOffset;
	private final AtomicBoolean shouldUpdateSubscriptions;
	private final AtomicBoolean shouldUnsubscribe;
	private final AtomicBoolean shouldGetSubscriptionList;

	private final Semaphore wakeUpSemaphore;

	private final ConsumerThreadSettings consumerThreadSettings;
	private final KafkaConsumer<String, byte[]> kafkaConsumer;

	private final Set<String> activeTopics;

	private final Map<String, CompletableFuture<Object>> asyncMethodResponsesMap;

	private interface DoNotDisturbeOperation<T> {
		Optional<T> perform();
	}

	public ConsumerThread(ConsumerThreadSettings consumerThreadSettings) {
		this.consumerThreadSettings = consumerThreadSettings;
		this.kafkaConsumer = consumerThreadSettings.getWrappedConsumer();

		activeTopics = new HashSet<>();
		shouldStopPolling = new AtomicBoolean(false);
		shouldCommitOffset = new AtomicBoolean(false);
		shouldUpdateSubscriptions = new AtomicBoolean(false);
		shouldUnsubscribe = new AtomicBoolean(false);
		shouldGetSubscriptionList = new AtomicBoolean(false);

		wakeUpSemaphore = new Semaphore(1);

		asyncMethodResponsesMap = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {
		IConsumerThreadCallback iWebSocketConsumerCallback = consumerThreadSettings.getiWebSocketConsumerCallback();
		Duration pollLoopDuration = consumerThreadSettings.getPollLoopDuration();
		ConsumerId consumerId = consumerThreadSettings.getiConsumerIdentifier();

		while (!shouldStopPolling.get()) {
			try {
				ConsumerRecords<String, byte[]> consumerRecords = kafkaConsumer.poll(pollLoopDuration);
				if (!consumerRecords.isEmpty()) {
					iWebSocketConsumerCallback.onRecordsReceived(consumerId, consumerRecords);
				}

			} catch (WakeupException exception) {
				Optional<MethodResult> optionalMethodResult = handleWakeupInterrupt();
				optionalMethodResult.ifPresent(methodResult ->
						asyncMethodResponsesMap.computeIfPresent(methodResult.getMethodName(), (key, value) -> {
							value.complete(methodResult.getResult());
							return value;
						})
				);

			} catch (Exception exception) {
				iWebSocketConsumerCallback.onConsumerError(consumerId, exception);
			}
		}
	}

	@Override
	public void commitSync() {
		doNotDisturbeOperaton(() -> {
			shouldCommitOffset.set(true);
			kafkaConsumer.wakeup();
			return Optional.empty();
		});
	}

	@Override
	public void stopPolling() {
		doNotDisturbeOperaton(() -> {
			shouldStopPolling.set(true);
			kafkaConsumer.wakeup();
			return Optional.empty();
		});
	}

	@Override
	public void updateSubscriptions(Set<String> topics) {
		doNotDisturbeOperaton(() -> {
			activeTopics.addAll(topics);
			shouldUpdateSubscriptions.set(true);
			kafkaConsumer.wakeup();
			return Optional.empty();
		});
	}

	@Override
	public void unsubscribe() {
		doNotDisturbeOperaton(() -> {
			activeTopics.clear();
			shouldUnsubscribe.set(true);
			kafkaConsumer.wakeup();
			return Optional.empty();
		});
	}

	@Override
	public Future<Object> getSubscriptions() {
		asyncMethodResponsesMap.put("getSubscriptions", new CompletableFuture<>());

		return doNotDisturbeOperaton(() -> {
			shouldGetSubscriptionList.set(true);
			kafkaConsumer.wakeup();
			return Optional.of(asyncMethodResponsesMap.get("getSubscriptions"));

		}).orElseGet(() -> CompletableFuture.completedFuture(Collections.emptySet()));
	}

	private Optional<MethodResult> handleWakeupInterrupt() {
		return doNotDisturbeOperaton(() -> {
			if (shouldStopPolling.get()) {
				kafkaConsumer.commitSync();

			} else if (shouldCommitOffset.get()) {
				kafkaConsumer.commitSync();
				shouldCommitOffset.set(false);

			} else if (shouldUpdateSubscriptions.get()) {
				kafkaConsumer.commitSync();
				kafkaConsumer.subscribe(activeTopics);
				shouldUpdateSubscriptions.set(false);

			} else if (shouldUnsubscribe.get()) {
				kafkaConsumer.commitSync();
				kafkaConsumer.unsubscribe();
				shouldUnsubscribe.set(false);

			} else if (shouldGetSubscriptionList.get()) {
				kafkaConsumer.commitSync();
				Set<String> subscriptions = kafkaConsumer.subscription();
				shouldGetSubscriptionList.set(false);
				return Optional.of(new MethodResult("getSubscriptions", subscriptions));
			}
			return Optional.empty();
		});
	}

	private <T> Optional<T> doNotDisturbeOperaton(DoNotDisturbeOperation<T> doNotDisturbeOperation) {
		try {
			wakeUpSemaphore.acquireUninterruptibly();
			return doNotDisturbeOperation.perform();

		} catch (Exception exception) {
			LOGGER.error("An error occurred inside thread {}: {}", getName(), exception.getMessage());
			return Optional.empty();

		} finally {
			wakeUpSemaphore.release();
		}
	}
}
