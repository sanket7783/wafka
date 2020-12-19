package com.wafka.factory.impl;

import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.ConsumerId;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.types.Protocol;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConsumerIdProtocol(Protocol.WEBSOCKET)
public class WebSocketConsumerIdFactory implements IConsumerIdFactory {
	private final Map<String, ConsumerId> flyweightConsumerIdMap;

	public WebSocketConsumerIdFactory() {
		flyweightConsumerIdMap = new ConcurrentHashMap<>();
	}

	@Override
	public ConsumerId getConsumerId(String identifier) {
		if (!flyweightConsumerIdMap.containsKey(identifier)) {
			ConsumerId consumerId = new ConsumerId(identifier, Protocol.WEBSOCKET);
			flyweightConsumerIdMap.putIfAbsent(identifier, consumerId);
		}
		return flyweightConsumerIdMap.get(identifier);
	}
}
