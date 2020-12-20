package com.wafka.model.response;

import com.wafka.types.ConsumerParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreatedConsumerOperationResponse extends OperationResponse {
	private static final long serialVersionUID = 8860347944604839931L;

	private Map<ConsumerParameter, Object> consumerParameters;

	public CreatedConsumerOperationResponse() {
		consumerParameters = new HashMap<>();
	}

	public Map<ConsumerParameter, Object> getConsumerParameters() {
		return Collections.unmodifiableMap(consumerParameters);
	}

	public void setConsumerParameters(Map<ConsumerParameter, Object> consumerParameters) {
		this.consumerParameters = consumerParameters;
	}
}
