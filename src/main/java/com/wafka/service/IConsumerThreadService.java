package com.wafka.service;

import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.ConsumerId;

import java.util.Set;

public interface IConsumerThreadService {
	void start(ConsumerId consumerId);

	void create(ConsumerThreadSettings consumerThreadSettings);

	boolean isRunning(ConsumerId consumerId);

	void stop(ConsumerId consumerId);

	void commitSync(ConsumerId consumerId);

	void subscribe(ConsumerId consumerId, Set<String> topics);

	void unsubscribe(ConsumerId consumerId);

	Set<String> getSubscriptions(ConsumerId consumerId);
}
