package com.wafka.exception;

import com.wafka.model.ConsumerId;

public class NoSuchConsumerThreadException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 8021176989702086896L;

	public NoSuchConsumerThreadException(ConsumerId consumerId) {
		super("No such consumer thread for consumer " + consumerId);
	}
}
