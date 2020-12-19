package com.wafka.service.impl;

import com.wafka.model.ConsumerId;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerService;
import com.wafka.service.IConsumerThreadService;
import com.wafka.types.OperationStatus;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class AutoConsumerOperationService implements IAutoConsumerOperationService {
	private final Logger logger;

	private final IConsumerService iConsumerService;

	private final IConsumerThreadService iConsumerThreadService;

	@Autowired
	public AutoConsumerOperationService(Logger logger, IConsumerService iConsumerService,
										IConsumerThreadService iConsumerThreadService) {

		this.logger = logger;
		this.iConsumerService = iConsumerService;
		this.iConsumerThreadService = iConsumerThreadService;
	}

	@Override
	public OperationStatus start(ConsumerId consumerId) {
		iConsumerThreadService.start(consumerId);
		return OperationStatus.SUCCESS;
	}

	@Override
	public boolean isRunning(ConsumerId consumerId) {
		return iConsumerThreadService.isRunning(consumerId);
	}

	@Override
	public OperationStatus stop(ConsumerId consumerId) {
		if (isRunning(consumerId)) {
			iConsumerThreadService.stop(consumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Closing Kafka consumer for consumer {}.", consumerId);
			kafkaConsumer.close();
			iConsumerService.remove(consumerId);
		});
	}

	@Override
	public OperationStatus commitSync(ConsumerId consumerId) {
		if (isRunning(consumerId)) {
			iConsumerThreadService.commitSync(consumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Committing offset sync for consumer {}.", consumerId);
			kafkaConsumer.commitSync();
		});
	}

	@Override
	public OperationStatus subscribe(ConsumerId consumerId, Set<String> topics) {
		if (isRunning(consumerId)) {
			iConsumerThreadService.subscribe(consumerId, topics);
			return OperationStatus.SUCCESS;
		}

		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Subscribing to topics {} for consumer {}.", topics, consumerId);
			kafkaConsumer.subscribe(topics);
		});
	}

	@Override
	public OperationStatus unsubscribe(ConsumerId consumerId) {
		if (isRunning(consumerId)) {
			iConsumerThreadService.unsubscribe(consumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Unsubscribing topics for consumer {}.", consumerId);
			kafkaConsumer.unsubscribe();
		});
	}

	@Override
	public Set<String> getSubscriptions(ConsumerId consumerId) {
		if (isRunning(consumerId)) {
			return iConsumerThreadService.getSubscriptions(consumerId);
		}

		logger.info("Getting subscriptions for consumer {}.", consumerId);
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(consumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			return new HashSet<>();
		}
		return kafkaConsumerOpt.get().subscription();
	}

	private OperationStatus doOperation(ConsumerId consumerId, Consumer<KafkaConsumer<?, ?>> consumer) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(consumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			return OperationStatus.FAIL;
		}
		consumer.accept(kafkaConsumerOpt.get());
		return OperationStatus.SUCCESS;
	}
}
