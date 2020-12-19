package com.wafka.factory;

import com.wafka.model.ConsumerId;

public interface IConsumerIdFactory {
	ConsumerId getConsumerId(String identifier);
}
