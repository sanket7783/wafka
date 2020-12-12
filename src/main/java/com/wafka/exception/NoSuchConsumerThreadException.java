package com.wafka.exception;

import com.wafka.model.IConsumerId;

public class NoSuchConsumerThreadException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 8021176989702086896L;

	public NoSuchConsumerThreadException(IConsumerId iConsumerId) {
		super("No such consumer thread for consumer " + iConsumerId);
	}
}
