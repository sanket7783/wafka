package com.wafka.exception;

public class MalformedCommandArgumentException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -2694020035803587084L;

	public MalformedCommandArgumentException(String message) {
		super(message);
	}
}
