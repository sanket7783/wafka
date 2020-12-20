package com.wafka.model.response;

import com.wafka.model.ConsumerId;
import com.wafka.types.OperationStatus;

public class OperationResponse extends Response implements IOperationResponse {
	private static final long serialVersionUID = -8049545195779798512L;

	private ConsumerId consumerId;

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
	public void setOperationStatus(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	@Override
	public OperationStatus getOperationStatus() {
		return operationStatus;
	}
}
