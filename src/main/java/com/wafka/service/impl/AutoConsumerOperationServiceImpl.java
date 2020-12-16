package com.wafka.service.impl;

import com.wafka.model.IConsumerId;
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
public class AutoConsumerOperationServiceImpl implements IAutoConsumerOperationService {
	private final Logger logger;

	private final IConsumerService iConsumerService;

	private final IConsumerThreadService iConsumerThreadService;

	@Autowired
	public AutoConsumerOperationServiceImpl(Logger logger, IConsumerService iConsumerService,
											IConsumerThreadService iConsumerThreadService) {

		this.logger = logger;
		this.iConsumerService = iConsumerService;
		this.iConsumerThreadService = iConsumerThreadService;
	}

	@Override
	public OperationStatus start(IConsumerId iConsumerId) {
		iConsumerThreadService.start(iConsumerId);
		return OperationStatus.SUCCESS;
	}

	@Override
	public boolean isRunning(IConsumerId iConsumerId) {
		return iConsumerThreadService.isRunning(iConsumerId);
	}

	@Override
	public OperationStatus stop(IConsumerId iConsumerId) {
		if (isRunning(iConsumerId)) {
			iConsumerThreadService.stop(iConsumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Closing Kafka consumer for consumer {}.", iConsumerId);
			kafkaConsumer.close();
			iConsumerService.remove(iConsumerId);
		});
	}

	@Override
	public OperationStatus commitSync(IConsumerId iConsumerId) {
		if (isRunning(iConsumerId)) {
			iConsumerThreadService.commitSync(iConsumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Committing offset sync for consumer {}.", iConsumerId);
			kafkaConsumer.commitSync();
		});
	}

	@Override
	public OperationStatus subscribe(IConsumerId iConsumerId, Set<String> topics) {
		if (isRunning(iConsumerId)) {
			iConsumerThreadService.subscribe(iConsumerId, topics);
			return OperationStatus.SUCCESS;
		}

		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Subscribing to topics {} for consumer {}.", topics, iConsumerId);
			kafkaConsumer.subscribe(topics);
		});
	}

	@Override
	public OperationStatus unsubscribe(IConsumerId iConsumerId) {
		if (isRunning(iConsumerId)) {
			iConsumerThreadService.unsubscribe(iConsumerId);
			return OperationStatus.SUCCESS;
		}

		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Unsubscribing topics for consumer {}.", iConsumerId);
			kafkaConsumer.unsubscribe();
		});
	}

	@Override
	public Set<String> getSubscriptions(IConsumerId iConsumerId) {
		if (isRunning(iConsumerId)) {
			return iConsumerThreadService.getSubscriptions(iConsumerId);
		}

		logger.info("Getting subscriptions for consumer {}.", iConsumerId);
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(iConsumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			return new HashSet<>();
		}
		return kafkaConsumerOpt.get().subscription();
	}

	private OperationStatus doOperation(IConsumerId iConsumerId, Consumer<KafkaConsumer<?, ?>> consumer) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(iConsumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			return OperationStatus.FAIL;
		}
		consumer.accept(kafkaConsumerOpt.get());
		return OperationStatus.SUCCESS;
	}
}
