package com.wafka.model;

import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;

import java.io.Serializable;

public interface IResponse extends Serializable {
	void setConsumerId(ConsumerId consumerId);

	ConsumerId getConsumerId();

	void setResponseType(ResponseType responseType);

	ResponseType getResponseType();

	void setMessage(String message);

	String getMessage();

	void setOperationStatus(OperationStatus operationStatus);

	OperationStatus getOperationStatus();
}
