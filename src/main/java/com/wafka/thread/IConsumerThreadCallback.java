package com.wafka.thread;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface IConsumerThreadCallback {
	void onRecordsReceived(ConsumerRecords<String, byte[]> consumerRecords);

	void onConsumerError(Throwable throwable);
}
