package com.wafka.exception;

public class CommandExecutionException extends Exception {
	private static final long serialVersionUID = 3104283402115469530L;

	public CommandExecutionException(Exception exception) {
		super(exception);
	}
}
