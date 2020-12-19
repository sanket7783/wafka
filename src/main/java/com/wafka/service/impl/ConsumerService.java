package com.wafka.service.impl;

import com.wafka.exception.ConsumerAlreadyCreatedException;
import com.wafka.exception.NoSuchConsumerException;
import com.wafka.model.ConsumerId;
import com.wafka.service.IConsumerService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerService implements IConsumerService {
	private final Map<ConsumerId, KafkaConsumer<String, byte[]>> kafkaConsumersMap;

	private final Logger logger;

	@Autowired
	public ConsumerService(Logger logger) {
		this.logger = logger;

		kafkaConsumersMap = new ConcurrentHashMap<>();
	}

	@Override
	public void create(ConsumerId consumerId, Properties consumerProperties) {
		if (kafkaConsumersMap.containsKey(consumerId)) {
			throw new ConsumerAlreadyCreatedException(consumerId);
		}

		KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(consumerProperties);
		kafkaConsumersMap.put(consumerId, kafkaConsumer);
		logger.info("Created Kafka consumer {}.", consumerId);
	}

	@Override
	public void remove(ConsumerId consumerId) {
		logger.info("Removing consumer {} from consumers map.", consumerId);
		kafkaConsumersMap.remove(consumerId);
	}

	@Override
	public Optional<KafkaConsumer<String, byte[]>> getConsumer(ConsumerId consumerId) {
		if (!kafkaConsumersMap.containsKey(consumerId)) {
			return Optional.empty();
		}
		return Optional.of(kafkaConsumersMap.get(consumerId));
	}

	@Override
	public KafkaConsumer<String, byte[]> getConsumerOrThrow(ConsumerId consumerId) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOptional = getConsumer(consumerId);
		if (!kafkaConsumerOptional.isPresent()) {
			throw new NoSuchConsumerException(consumerId);
		}
		return kafkaConsumerOptional.get();
	}

	@Override
	public Set<ConsumerId> getRegisteredConsumers() {
		return Collections.unmodifiableSet(kafkaConsumersMap.keySet());
	}
}
