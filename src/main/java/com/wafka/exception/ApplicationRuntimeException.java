package com.wafka.exception;

public class ApplicationRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 7351691249467222405L;

	public ApplicationRuntimeException(String message) {
		super(message);
	}
}
