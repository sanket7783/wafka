package com.wafka.exception;

import com.wafka.model.ConsumerId;

public class ConsumerThreadAlreadyCreatedException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 626388790253841620L;

	public ConsumerThreadAlreadyCreatedException(ConsumerId consumerId) {
		super("Consumer thread already created for consumer: " + consumerId);
	}
}
