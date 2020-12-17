package com.wafka.thread;

import com.wafka.model.IConsumerId;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface IConsumerThreadCallback {
	void onRecordsReceived(IConsumerId iConsumerId, ConsumerRecords<String, byte[]> consumerRecords);

	void onConsumerError(IConsumerId iConsumerId, Throwable throwable);
}
