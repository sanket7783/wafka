package com.wafka.service;

import com.wafka.model.IConsumerId;
import com.wafka.types.OperationStatus;

import java.util.Collection;
import java.util.Set;

public interface IConsumerOperationService {
	OperationStatus subscribe(IConsumerId iConsumerId, Collection<String> topics);

	OperationStatus stop(IConsumerId iConsumerId);

	OperationStatus commitSync(IConsumerId iConsumerId);

	OperationStatus unsubscribe(IConsumerId iConsumerId);

	Set<String> getSubscriptions(IConsumerId iConsumerId);
}
