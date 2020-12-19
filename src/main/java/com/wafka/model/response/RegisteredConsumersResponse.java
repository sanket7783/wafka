package com.wafka.model.response;

import com.wafka.model.ConsumerId;

import java.util.HashSet;
import java.util.Set;

public class RegisteredConsumersResponse extends Response {
	private static final long serialVersionUID = 1557561113940178185L;

	private Set<ConsumerId> consumers;

	public RegisteredConsumersResponse() {
		consumers = new HashSet<>();
	}

	public Set<ConsumerId> getConsumers() {
		return consumers;
	}

	public void setConsumers(Set<ConsumerId> consumers) {
		this.consumers = consumers;
	}
}
