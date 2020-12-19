package com.wafka.exception;

import com.wafka.model.ConsumerId;

public class ConsumerAlreadyCreatedException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -5823301711132395805L;

	public ConsumerAlreadyCreatedException(ConsumerId consumerId) {
		super("A consumer with id " + consumerId + " already exists. Choose another identifier.");
	}
}
