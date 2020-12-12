package com.wafka.service;

import com.wafka.model.IConsumerId;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public interface IConsumerService {
	void create(IConsumerId iConsumerId, Properties consumerProperties);

	void remove(IConsumerId iConsumerId);

	Optional<KafkaConsumer<String, byte[]>> getConsumer(IConsumerId iConsumerId);

	KafkaConsumer<String, byte[]> getConsumerOrThrow(IConsumerId iConsumerId);

	Set<IConsumerId> getRegisteredConsumers();
}
