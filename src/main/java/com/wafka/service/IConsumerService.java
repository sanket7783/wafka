package com.wafka.service;

import com.wafka.model.ConsumerId;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public interface IConsumerService {
	void create(ConsumerId consumerId, Properties consumerProperties);

	void remove(ConsumerId consumerId);

	Optional<KafkaConsumer<String, byte[]>> getConsumer(ConsumerId consumerId);

	KafkaConsumer<String, byte[]> getConsumerOrThrow(ConsumerId consumerId);

	Set<ConsumerId> getRegisteredConsumers();
}
