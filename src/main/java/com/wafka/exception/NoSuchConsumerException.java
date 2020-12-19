package com.wafka.exception;

import com.wafka.model.ConsumerId;

public class NoSuchConsumerException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -8508838814840177959L;

	public NoSuchConsumerException(ConsumerId consumerId) {
		super("No such consumer with identifier: " + consumerId);
	}
}
