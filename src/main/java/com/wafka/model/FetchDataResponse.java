package com.wafka.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FetchDataResponse extends DefaultResponse {
	private static final long serialVersionUID = -6103467784487403800L;

	private final List<FetchedContent> fetchedContents;

	public FetchDataResponse() {
		this.fetchedContents = new ArrayList<>();
	}

	public FetchDataResponse(List<FetchedContent> fetchedContents) {
		this.fetchedContents = fetchedContents;
	}

	public List<FetchedContent> getFetchedContents() {
		return Collections.unmodifiableList(fetchedContents);
	}
}
