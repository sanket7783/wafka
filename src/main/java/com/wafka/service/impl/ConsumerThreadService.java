package com.wafka.service.impl;

import com.wafka.exception.ConsumerThreadAlreadyCreatedException;
import com.wafka.exception.NoSuchConsumerThreadException;
import com.wafka.factory.IConsumerThreadFactory;
import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.ConsumerId;
import com.wafka.service.IConsumerThreadService;
import com.wafka.thread.AbstractConsumerThread;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class ConsumerThreadService implements IConsumerThreadService {
	private final Map<ConsumerId, AbstractConsumerThread> consumerThreadMap;

	private final Logger logger;

	private final IConsumerThreadFactory iConsumerThreadFactory;

	@Autowired
	public ConsumerThreadService(Logger logger, IConsumerThreadFactory iConsumerThreadFactory) {
		this.logger = logger;
		this.iConsumerThreadFactory = iConsumerThreadFactory;

		consumerThreadMap = new ConcurrentHashMap<>();
	}

	@Override
	public void start(ConsumerId consumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
		if (!abstractConsumerThread.isAlive()) {
			logger.info("Starting async consumer for client {}.", consumerId);
			abstractConsumerThread.start();
		} else {
			logger.warn("Consumer thread for client {} is already running!", consumerId);
		}
	}

	@Override
	public void create(ConsumerThreadSettings consumerThreadSettings) {
		ConsumerId consumerId = consumerThreadSettings.getiConsumerIdentifier();
		if (consumerThreadMap.containsKey(consumerId)) {
			throw new ConsumerThreadAlreadyCreatedException(consumerId);
		}

		AbstractConsumerThread abstractConsumerThread = iConsumerThreadFactory.getThread(consumerThreadSettings);
		consumerThreadMap.put(consumerId, abstractConsumerThread);
		logger.info("Created consumer thread for client {}.", consumerId);
	}

	@Override
	public boolean isRunning(ConsumerId consumerId) {
		if (consumerThreadMap.containsKey(consumerId)) {
			AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
			return abstractConsumerThread.isAlive();
		}
		return false;
	}

	@Override
	public void stop(ConsumerId consumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
		abstractConsumerThread.stopPolling();
		logger.info("Consumer {} thread has been stopped.", consumerId);

		try {
			abstractConsumerThread.join();
			logger.info("Consumer {} thread has joined.", consumerId);

		} catch (InterruptedException exception) {
			logger.warn("Consumer {} thread has been interrupted.", consumerId);
			Thread.currentThread().interrupt();

		} finally {
			consumerThreadMap.remove(consumerId);
			logger.info("Consumer {} thread has been removed from consumers map.", consumerId);
		}
	}

	@Override
	public void commitSync(ConsumerId consumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
		abstractConsumerThread.commitSync();
	}

	@Override
	public void subscribe(ConsumerId consumerId, Set<String> topics) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
		logger.info("Subscribing to topics {} for consumer {}.", topics, consumerId);
		abstractConsumerThread.updateSubscriptions(topics);
	}

	@Override
	public void unsubscribe(ConsumerId consumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);
		logger.info("Unsubscribing consumer {}.", consumerId);
		abstractConsumerThread.unsubscribe();
	}

	@Override
	@SuppressWarnings("unchecked cast")
	public Set<String> getSubscriptions(ConsumerId consumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(consumerId);

		Future<Object> operationFuture = abstractConsumerThread.getSubscriptions();
		try {
			return (Set<String>)operationFuture.get();
		} catch (Exception exception) {
			logger.error("Error while getting subscriptions: {}", exception.getMessage());
			return Collections.emptySet();
		}
	}

	private AbstractConsumerThread getConsumerThreadOrThrow(ConsumerId consumerId) {
		if (!consumerThreadMap.containsKey(consumerId)) {
			throw new NoSuchConsumerThreadException(consumerId);
		}
		return consumerThreadMap.get(consumerId);
	}
}
