package com.wafka.service.impl;

import com.wafka.exception.ConsumerThreadAlreadyCreatedException;
import com.wafka.exception.NoSuchConsumerThreadException;
import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.IConsumerId;
import com.wafka.service.IConsumerThreadService;
import com.wafka.thread.AbstractConsumerThread;
import com.wafka.thread.impl.ConsumerThreadImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerThreadServiceImpl implements IConsumerThreadService {
	private final Map<IConsumerId, AbstractConsumerThread> consumerThreadMap;

	private final Logger logger;

	@Autowired
	public ConsumerThreadServiceImpl(Logger logger) {
		this.logger = logger;

		consumerThreadMap = new ConcurrentHashMap<>();
	}

	@Override
	public void start(IConsumerId iConsumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
		if (!abstractConsumerThread.isAlive()) {
			logger.info("Starting async consumer for client {}.", iConsumerId);
			abstractConsumerThread.start();
		} else {
			logger.warn("Consumer thread for client {} is already running!", iConsumerId);
		}
	}

	@Override
	public void create(ConsumerThreadSettings consumerThreadSettings) {
		IConsumerId iConsumerId = consumerThreadSettings.getiConsumerIdentifier();
		if (consumerThreadMap.containsKey(iConsumerId)) {
			throw new ConsumerThreadAlreadyCreatedException(iConsumerId);
		}

		ConsumerThreadImpl kafkaConsumerThread = new ConsumerThreadImpl(consumerThreadSettings);
		consumerThreadMap.put(iConsumerId, kafkaConsumerThread);
		logger.info("Created consumer thread for client {}.", iConsumerId);
	}

	@Override
	public boolean isRunning(IConsumerId iConsumerId) {
		if (consumerThreadMap.containsKey(iConsumerId)) {
			AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
			return abstractConsumerThread.isAlive();
		}
		return false;
	}

	@Override
	public void stop(IConsumerId iConsumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
		abstractConsumerThread.stopPolling();
		logger.info("Consumer {} thread has been stopped.", iConsumerId);

		try {
			abstractConsumerThread.join();
			logger.info("Consumer {} thread has joined.", iConsumerId);

		} catch (InterruptedException exception) {
			logger.warn("Consumer {} thread has been interrupted.", iConsumerId);
			Thread.currentThread().interrupt();

		} finally {
			consumerThreadMap.remove(iConsumerId);
			logger.info("Consumer {} thread has been removed from consumers map.", iConsumerId);
		}
	}

	@Override
	public void commitSync(IConsumerId iConsumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
		abstractConsumerThread.commitSync();
	}

	@Override
	public void subscribe(IConsumerId iConsumerId, Collection<String> topics) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
		logger.info("Subscribing to topics {} for consumer {}.", topics, iConsumerId);
		abstractConsumerThread.updateSubscriptions(topics);
	}

	@Override
	public void unsubscribe(IConsumerId iConsumerId) {
		AbstractConsumerThread abstractConsumerThread = getConsumerThreadOrThrow(iConsumerId);
		logger.info("Unsubscribing consumer {}.", iConsumerId);
		abstractConsumerThread.unsubscribe();
	}

	private AbstractConsumerThread getConsumerThreadOrThrow(IConsumerId iConsumerId) {
		if (!consumerThreadMap.containsKey(iConsumerId)) {
			throw new NoSuchConsumerThreadException(iConsumerId);
		}
		return consumerThreadMap.get(iConsumerId);
	}
}
