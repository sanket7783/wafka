package com.wafka.model.response;

import com.wafka.model.ConsumerId;
import com.wafka.types.OperationStatus;

public interface IOperationResponse extends IResponse {
	void setConsumerId(ConsumerId consumerId);

	ConsumerId getConsumerId();

	void setOperationStatus(OperationStatus operationStatus);

	OperationStatus getOperationStatus();
}
