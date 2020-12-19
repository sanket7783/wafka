package com.wafka.thread;

import com.wafka.model.ConsumerId;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface IConsumerThreadCallback {
	void onRecordsReceived(ConsumerId consumerId, ConsumerRecords<String, byte[]> consumerRecords);

	void onConsumerError(ConsumerId consumerId, Throwable throwable);
}
