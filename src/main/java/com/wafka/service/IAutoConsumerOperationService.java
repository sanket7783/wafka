package com.wafka.service;

import com.wafka.model.IConsumerId;
import com.wafka.types.OperationStatus;

public interface IAutoConsumerOperationService extends IConsumerOperationService {
	OperationStatus start(IConsumerId iConsumerId);

	boolean isRunning(IConsumerId iConsumerId);
}
