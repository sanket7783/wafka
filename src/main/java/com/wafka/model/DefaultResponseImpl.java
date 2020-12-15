package com.wafka.model;

import com.wafka.types.ResponseType;

public class DefaultResponseImpl implements IResponse {
	private static final long serialVersionUID = -8049545195779798512L;

	private ResponseType responseType;

	private String message;

	@Override
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	@Override
	public ResponseType getResponseType() {
		return responseType;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
