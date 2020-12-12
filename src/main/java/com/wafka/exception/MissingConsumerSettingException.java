package com.wafka.exception;

import com.wafka.types.ConsumerParameter;

public class MissingConsumerSettingException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 6096194749748194462L;

	public MissingConsumerSettingException(String parameterName) {
		super("Missing consumer setting: " + parameterName);
	}

	public MissingConsumerSettingException(ConsumerParameter consumerParameterKey) {
		this(consumerParameterKey.getDescription());
	}
}
