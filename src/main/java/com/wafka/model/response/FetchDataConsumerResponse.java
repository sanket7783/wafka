package com.wafka.model.response;

import com.wafka.model.FetchedContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FetchDataConsumerResponse extends ConsumerResponse {
	private static final long serialVersionUID = -6103467784487403800L;

	private final List<FetchedContent> fetchedContents;

	public FetchDataConsumerResponse() {
		this.fetchedContents = new ArrayList<>();
	}

	public FetchDataConsumerResponse(List<FetchedContent> fetchedContents) {
		this.fetchedContents = fetchedContents;
	}

	public List<FetchedContent> getFetchedContents() {
		return Collections.unmodifiableList(fetchedContents);
	}
}
