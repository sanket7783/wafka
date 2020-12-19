package com.wafka.service.impl;

import com.wafka.model.ConsumerId;
import com.wafka.service.IConsumerWebSocketSessionService;
import org.springframework.stereotype.Service;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerWebSocketSessionService implements IConsumerWebSocketSessionService {
	private final Map<ConsumerId, Session> websocketSessionMap;

	public ConsumerWebSocketSessionService() {
		websocketSessionMap = new ConcurrentHashMap<>();
	}

	@Override
	public void store(ConsumerId consumerId, Session session) {
		websocketSessionMap.putIfAbsent(consumerId, session);
	}

	@Override
	public void close(ConsumerId consumerId, CloseReason closeReason) throws IOException {
		Session session = websocketSessionMap.get(consumerId);
		if (session != null && session.isOpen()) {
			session.close(closeReason);
		}
	}

	@Override
	public void delete(ConsumerId consumerId) {
		websocketSessionMap.remove(consumerId);
	}
}
