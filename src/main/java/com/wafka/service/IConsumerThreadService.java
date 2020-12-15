package com.wafka.service;

import com.wafka.model.IConsumerId;
import com.wafka.model.ConsumerThreadSettings;

import java.util.Collection;
import java.util.Set;

public interface IConsumerThreadService {
	void start(IConsumerId iConsumerId);

	void create(ConsumerThreadSettings consumerThreadSettings);

	boolean isRunning(IConsumerId iConsumerId);

	void stop(IConsumerId iConsumerId);

	void commitSync(IConsumerId iConsumerId);

	void subscribe(IConsumerId iConsumerId, Collection<String> topics);

	void unsubscribe(IConsumerId iConsumerId);

	Set<String> getSubscriptions(IConsumerId iConsumerId);
}
