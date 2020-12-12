package com.wafka.factory;

import com.wafka.types.ConsumerParameter;

import java.util.Map;
import java.util.Properties;

public interface IConsumerPropertyFactory {
	Properties getProperties(Map<ConsumerParameter, Object> parametersMap);
}
