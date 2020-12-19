package com.wafka.model.response;

import com.wafka.types.ResponseType;

public class Response implements IResponse {
	private static final long serialVersionUID = -1720690636654752643L;

	private ResponseType responseType;

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	public ResponseType getResponseType() {
		return responseType;
	}
}
