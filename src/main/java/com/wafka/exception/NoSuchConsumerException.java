package com.wafka.exception;

import com.wafka.model.IConsumerId;

public class NoSuchConsumerException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -8508838814840177959L;

	public NoSuchConsumerException(IConsumerId iConsumerId) {
		super("No such client with identifier: " + iConsumerId);
	}
}
