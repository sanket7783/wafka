package com.wafka.model;

import com.wafka.types.ResponseType;

import java.io.Serializable;

public interface IResponse extends Serializable {
	void setConsumerId(IConsumerId consumerId);

	IConsumerId getConsumerId();

	void setResponseType(ResponseType responseType);

	ResponseType getResponseType();

	void setMessage(String message);

	String getMessage();
}
