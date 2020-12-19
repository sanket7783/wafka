package com.wafka.service;

import com.wafka.model.ConsumerId;
import com.wafka.types.OperationStatus;

import java.util.Set;

public interface IConsumerOperationService {
	OperationStatus subscribe(ConsumerId consumerId, Set<String> topics);

	OperationStatus stop(ConsumerId consumerId);

	OperationStatus commitSync(ConsumerId consumerId);

	OperationStatus unsubscribe(ConsumerId consumerId);

	Set<String> getSubscriptions(ConsumerId consumerId);
}
