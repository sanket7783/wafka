package com.wafka.service.impl;

import com.wafka.exception.ConsumerAlreadyCreatedException;
import com.wafka.exception.NoSuchConsumerException;
import com.wafka.model.IConsumerId;
import com.wafka.service.IConsumerService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerServiceImpl implements IConsumerService {
	private final Map<IConsumerId, KafkaConsumer<String, byte[]>> kafkaConsumersMap;

	private final Logger logger;

	@Autowired
	public ConsumerServiceImpl(Logger logger) {
		this.logger = logger;

		kafkaConsumersMap = new ConcurrentHashMap<>();
	}

	@Override
	public void create(IConsumerId iConsumerId, Properties consumerProperties) {
		if (kafkaConsumersMap.containsKey(iConsumerId)) {
			throw new ConsumerAlreadyCreatedException(iConsumerId);
		}

		KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(consumerProperties);
		kafkaConsumersMap.put(iConsumerId, kafkaConsumer);
		logger.info("Created Kafka consumer {}.", iConsumerId);
	}

	@Override
	public void remove(IConsumerId iConsumerId) {
		logger.info("Removing consumer {} from consumers map.", iConsumerId);
		kafkaConsumersMap.remove(iConsumerId);
	}

	@Override
	public Optional<KafkaConsumer<String, byte[]>> getConsumer(IConsumerId iConsumerId) {
		if (!kafkaConsumersMap.containsKey(iConsumerId)) {
			return Optional.empty();
		}
		return Optional.of(kafkaConsumersMap.get(iConsumerId));
	}

	@Override
	public KafkaConsumer<String, byte[]> getConsumerOrThrow(IConsumerId iConsumerId) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOptional = getConsumer(iConsumerId);
		if (!kafkaConsumerOptional.isPresent()) {
			throw new NoSuchConsumerException(iConsumerId);
		}
		return kafkaConsumerOptional.get();
	}

	@Override
	public Set<IConsumerId> getRegisteredConsumers() {
		return kafkaConsumersMap.keySet();
	}
}
