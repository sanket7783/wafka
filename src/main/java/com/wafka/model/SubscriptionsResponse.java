package com.wafka.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionsResponse extends DefaultResponse {
	private static final long serialVersionUID = 5127500883730591282L;

	private Set<String> subscriptions;

	public SubscriptionsResponse() {
		subscriptions = new HashSet<>();
	}

	public SubscriptionsResponse(Set<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public void setSubscriptions(Set<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Set<String> getSubscriptions() {
		return Collections.unmodifiableSet(subscriptions);
	}
}
