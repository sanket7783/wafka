package com.wafka.thread.impl;

import com.wafka.model.ConsumerThreadSettings;
import com.wafka.thread.AbstractConsumerThread;
import com.wafka.thread.IConsumerThreadCallback;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerThreadImpl extends AbstractConsumerThread {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThreadImpl.class);

	private final AtomicBoolean shouldStopPolling;
	private final AtomicBoolean shouldCommitOffset;
	private final AtomicBoolean shouldUpdateSubscriptions;
	private final AtomicBoolean shouldUnsubscribe;

	private final Semaphore wakeUpSemaphore;

	private final ConsumerThreadSettings consumerThreadSettings;
	private final KafkaConsumer<String, byte[]> kafkaConsumer;

	private final Collection<String> activeTopics;

	private interface AtomicOperation {
		void perform();
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

		wakeUpSemaphore = new Semaphore(1);
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
				handleWakeupInterrupt();

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
		});
	}

	@Override
	public void stopPolling() {
		doNotDisturbeOperaton(() -> {
			shouldStopPolling.set(true);
			kafkaConsumer.wakeup();
		});
	}

	@Override
	public void updateSubscriptions(Collection<String> topics) {
		doNotDisturbeOperaton(() -> {
			activeTopics.addAll(topics);
			shouldUpdateSubscriptions.set(true);
			kafkaConsumer.wakeup();
		});
	}

	@Override
	public void unsubscribe() {
		doNotDisturbeOperaton(() -> {
			activeTopics.clear();
			shouldUnsubscribe.set(true);
			kafkaConsumer.wakeup();
		});
	}

	private void handleWakeupInterrupt() {
		doNotDisturbeOperaton(() -> {
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
			}
		});
	}
	private void doNotDisturbeOperaton(AtomicOperation atomicOperation) {
		try {
			wakeUpSemaphore.acquireUninterruptibly();
			atomicOperation.perform();

		} catch (Exception exception) {
			LOGGER.error("An error occurred inside thread {}: {}", getName(), exception.getMessage());

		} finally {
			wakeUpSemaphore.release();
		}
	}
}
