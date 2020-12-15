package com.wafka.model;

import java.util.Collections;
import java.util.Set;

public class SubscriptionsResponseImpl extends DefaultResponseImpl {
	private static final long serialVersionUID = 5127500883730591282L;

	private final Set<String> subscriptions;

	public SubscriptionsResponseImpl(Set<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Set<String> getSubscriptions() {
		return Collections.unmodifiableSet(subscriptions);
	}
}
