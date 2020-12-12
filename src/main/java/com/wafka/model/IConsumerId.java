package com.wafka.model;

import com.wafka.types.Protocol;

public interface IConsumerId {
	String getIdentifier();

	Protocol getProtocolType();
}
