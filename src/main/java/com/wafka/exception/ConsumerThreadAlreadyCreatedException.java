package com.wafka.exception;

import com.wafka.model.IConsumerId;

public class ConsumerThreadAlreadyCreatedException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 626388790253841620L;

	public ConsumerThreadAlreadyCreatedException(IConsumerId iConsumerId) {
		super("Consumer thread already created for consumer: " + iConsumerId);
	}
}
