package com.wafka.service.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.service.IConsumerService;
import com.wafka.service.IManualConsumerOperationService;
import com.wafka.types.OperationStatus;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
	public List<FetchedContent> fetch(ConsumerId consumerId, int pollDuration) {
		KafkaConsumer<String, byte[]> kafkaConsumer = iConsumerService.getConsumerOrThrow(consumerId);
		logger.info("Fetching data for consumer {}.", consumerId);

		Duration oneSecond = Duration.ofSeconds(pollDuration);
		ConsumerRecords<String, byte[]> consumerRecords =  kafkaConsumer.poll(oneSecond);
		return iFetchedContentFactory.getContents(consumerRecords);
	}

	@Override
	public OperationStatus subscribe(ConsumerId consumerId, Set<String> topics) {
		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Subscribing to topics {} for consumer {}.", topics, consumerId);
			kafkaConsumer.subscribe(topics);
		});
	}

	@Override
	public OperationStatus stop(ConsumerId consumerId) {
		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Closing Kafka consumer for consumer {}.", consumerId);
			kafkaConsumer.close();
			iConsumerService.remove(consumerId);
		});
	}

	@Override
	public OperationStatus commitSync(ConsumerId consumerId) {
		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Committing offset sync for consumer {}.", consumerId);
			kafkaConsumer.commitSync();
		});
	}

	@Override
	public OperationStatus unsubscribe(ConsumerId consumerId) {
		return doOperation(consumerId, kafkaConsumer -> {
			logger.info("Unsubscribing for consumer {}.", consumerId);
			kafkaConsumer.unsubscribe();
		});
	}

	@Override
	public Set<String> getSubscriptions(ConsumerId consumerId) {
		logger.info("Getting subscriptions for consumer {}.", consumerId);
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOpt = iConsumerService.getConsumer(consumerId);
		if (!kafkaConsumerOpt.isPresent()) {
			logger.warn("Consumer {} not found!", consumerId);
			return new HashSet<>();
		}
		return kafkaConsumerOpt.get().subscription();
	}

	private OperationStatus doOperation(ConsumerId consumerId, Consumer<KafkaConsumer<String, byte[]>> consumer) {
		Optional<KafkaConsumer<String, byte[]>> kafkaConsumerOptional = iConsumerService.getConsumer(consumerId);
		if (!kafkaConsumerOptional.isPresent()) {
			return OperationStatus.FAIL;
		}
		consumer.accept(kafkaConsumerOptional.get());
		return OperationStatus.SUCCESS;
	}
}
