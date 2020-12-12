package com.wafka.exception;

import com.wafka.model.IConsumerId;

public class ConsumerAlreadyCreatedException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -5823301711132395805L;

	public ConsumerAlreadyCreatedException(IConsumerId iConsumerId) {
		super("A consumer with id " + iConsumerId + " already exists. Choose another identifier.");
	}
}
