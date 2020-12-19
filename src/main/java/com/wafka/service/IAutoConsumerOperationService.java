package com.wafka.service;

import com.wafka.model.ConsumerId;
import com.wafka.types.OperationStatus;

public interface IAutoConsumerOperationService extends IConsumerOperationService {
	OperationStatus start(ConsumerId consumerId);

	boolean isRunning(ConsumerId consumerId);
}
