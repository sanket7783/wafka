package com.wafka.model;

import com.wafka.types.Protocol;

import java.util.Objects;

public class ConsumerIdImpl implements IConsumerId {
	private final String identifier;

	private final Protocol protocol;

	public ConsumerIdImpl(String identifier, Protocol protocol) {
		this.identifier = identifier;
		this.protocol = protocol;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public Protocol getProtocolType() {
		return protocol;
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConsumerIdImpl that = (ConsumerIdImpl) o;
		return identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}
}