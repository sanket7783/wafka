package com.wafka.model;

import java.util.Collections;
import java.util.List;

public class FetchDataResponseImpl extends DefaultResponseImpl {
	private static final long serialVersionUID = -6103467784487403800L;

	private final List<IFetchedContent> fetchedContents;

	public FetchDataResponseImpl(List<IFetchedContent> fetchedContents) {
		this.fetchedContents = fetchedContents;
	}

	public List<IFetchedContent> getFetchedContents() {
		return Collections.unmodifiableList(fetchedContents);
	}
}
