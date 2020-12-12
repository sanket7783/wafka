package com.wafka.exception;

public class InvalidTopicListArgumentException extends MalformedCommandArgumentException {
	private static final long serialVersionUID = -900206319876537571L;

	public InvalidTopicListArgumentException() {
		super("Topic list must be a valid json list of strings!");
	}
}
