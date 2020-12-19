package com.wafka.model.response;

import com.wafka.types.ResponseType;

import java.io.Serializable;

public interface IResponse extends Serializable {
	void setResponseType(ResponseType responseType);

	ResponseType getResponseType();
}
