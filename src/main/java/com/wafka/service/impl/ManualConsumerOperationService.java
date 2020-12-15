package com.wafka.service.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.IConsumerId;
import com.wafka.model.IFetchedContent;
import com.wafka.service.IConsumerService;
import com.wafka.service.IManualConsumerOperationService;
import com.wafka.types.OperationStatus;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

@Service
public class ManualConsumerOperationService implements IManualConsumerOperationService {
	private final Logger logger;

	private final IConsumerService iConsumerService;

	private final IFetchedContentFactory iFetchedContentFactory;

	@Autowired
	public ManualConsumerOperationService(Logger logger, IConsumerService iConsumerService,
										  IFetchedContentFactory iFetchedContentFactory) {

		this.logger = logger;
		this.iConsumerService = iConsumerService;
		this.iFetchedContentFactory = iFetchedContentFactory;
	}

	@Override
	public List<IFetchedContent> fetch(IConsumerId iConsumerId, int pollDuration) {
		KafkaConsumer<String, byte[]> kafkaConsumer = iConsumerService.getConsumerOrThrow(iConsumerId);

		logger.info("Fetching data for consumer {}.", iConsumerId);
		Duration oneSecond = Duration.ofSeconds(pollDuration);
		ConsumerRecords<String, byte[]> consumerRecords =  kafkaConsumer.poll(oneSecond);
		return iFetchedContentFactory.getContents(consumerRecords);
	}

	@Override
	public OperationStatus subscribe(IConsumerId iConsumerId, Collection<String> topics) {
		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Subscribing to topics {} for consumer {}.", topics, iConsumerId);
			kafkaConsumer.subscribe(topics);
		});
	}

	@Override
	public OperationStatus stop(IConsumerId iConsumerId) {
		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Closing Kafka consumer for consumer {}.", iConsumerId);
			kafkaConsumer.close();
			iConsumerService.remove(iConsumerId);
		});
	}

	@Override
	public OperationStatus commitSync(IConsumerId iConsumerId) {
		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Committing offset sync for consumer {}.", iConsumerId);
			kafkaConsumer.commitSync();
		});
	}

	@Override
	public OperationStatus unsubscribe(IConsumerId iConsumerId) {
		return doOperation(iConsumerId, kafkaConsumer -> {
			logger.info("Unsubscribing for consumer {}.", iConsumerId);
			kafkaConsumer.unsubscribe();
		});
	}

	@Override
	public Set<String> getSubscriptions(IConsumerId iConsumerId) {
		logger.info("Getting subscriptions for consumer {}.", iConsumerId);
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(iConsumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			return new HashSet<>();
		}
		return kafkaConsumerOpt.get().subscription();
	}

	private OperationStatus doOperation(IConsumerId iConsumerId, Consumer<KafkaConsumer<String, byte[]>> consumer) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOptional = iConsumerService.getConsumer(iConsumerId);
		if (!kafkaConsumerOptional.isPresent()) {
			return OperationStatus.FAIL;
		}
		consumer.accept(kafkaConsumerOptional.get());
		return OperationStatus.SUCCESS;
	}
}
