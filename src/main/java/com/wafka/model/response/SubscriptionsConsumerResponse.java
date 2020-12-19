package com.wafka.model.response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionsConsumerResponse extends ConsumerResponse {
	private static final long serialVersionUID = 5127500883730591282L;

	private Set<String> subscriptions;

	public SubscriptionsConsumerResponse() {
		subscriptions = new HashSet<>();
	}

	public SubscriptionsConsumerResponse(Set<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public void setSubscriptions(Set<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Set<String> getSubscriptions() {
		return Collections.unmodifiableSet(subscriptions);
	}
}
