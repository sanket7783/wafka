package com.wafka.model;

import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;

public class DefaultResponse implements IResponse {
	private static final long serialVersionUID = -8049545195779798512L;

	private ConsumerId consumerId;

	private ResponseType responseType;

	private String message;

	private OperationStatus operationStatus;

	@Override
	public void setConsumerId(ConsumerId consumerId) {
		this.consumerId = consumerId;
	}

	@Override
	public ConsumerId getConsumerId() {
		return consumerId;
	}

	@Override
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	@Override
	public ResponseType getResponseType() {
		return responseType;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setOperationStatus(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	@Override
	public OperationStatus getOperationStatus() {
		return operationStatus;
	}
}
