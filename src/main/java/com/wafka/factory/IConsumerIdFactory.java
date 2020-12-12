package com.wafka.factory;

import com.wafka.model.IConsumerId;

public interface IConsumerIdFactory {
	IConsumerId getConsumerId(String identifier);
}
