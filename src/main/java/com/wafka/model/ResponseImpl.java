package com.wafka.model;

import com.wafka.types.ResponseType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResponseImpl implements IResponse {
	private static final long serialVersionUID = -6103467784487403800L;

	private ResponseType responseType;

	private String message;

	private List<IFetchedContent> fetchedContents;

	public ResponseImpl() {
		this.fetchedContents = new ArrayList<>();
	}

	@Override
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setFetchedContents(List<IFetchedContent> fetchedContents) {
		this.fetchedContents = Collections.unmodifiableList(fetchedContents);
	}

	@Override
	public ResponseType getResponseType() {
		return responseType;
	}

	@Override
	public List<IFetchedContent> getFetchedContents() {
		return fetchedContents;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
