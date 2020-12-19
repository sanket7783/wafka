package com.wafka.model;

import com.wafka.types.Protocol;

import java.io.Serializable;
import java.util.Objects;

public class ConsumerId implements Serializable {
	private static final long serialVersionUID = -8662939182587591266L;

	private String identifier;

	private Protocol protocol;

	public ConsumerId() {
		this.identifier = null;
		this.protocol = null;
	}

	public ConsumerId(String identifier, Protocol protocol) {
		this.identifier = identifier;
		this.protocol = protocol;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public String toString() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConsumerId that = (ConsumerId) o;
		return identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}
}
