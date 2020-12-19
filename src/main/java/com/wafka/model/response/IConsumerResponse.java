package com.wafka.model.response;

import com.wafka.model.ConsumerId;
import com.wafka.types.OperationStatus;

import java.io.Serializable;

public interface IConsumerResponse extends Serializable {
	void setConsumerId(ConsumerId consumerId);

	ConsumerId getConsumerId();

	void setOperationStatus(OperationStatus operationStatus);

	OperationStatus getOperationStatus();
}
