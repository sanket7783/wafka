package com.wafka.model;

import com.wafka.types.ResponseType;

import java.util.List;

public interface IResponse {
	void setResponseType(ResponseType responseType);

	ResponseType getResponseType();

	void setFetchedContents(List<IFetchedContent> fetchedContents);

	List<IFetchedContent> getFetchedContents();

	void setMessage(String message);

	String getMessage();
}
