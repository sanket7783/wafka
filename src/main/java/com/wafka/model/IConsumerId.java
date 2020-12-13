package com.wafka.model;

import com.wafka.types.Protocol;

import java.io.Serializable;

public interface IConsumerId extends Serializable {
	String getIdentifier();

	Protocol getProtocolType();
}
