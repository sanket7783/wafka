package com.wafka.service.impl;

import com.wafka.model.ConsumerId;
import com.wafka.service.IConsumerWebSocketSessionService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerWebSocketSessionService implements IConsumerWebSocketSessionService {
	private final Map<ConsumerId, WebSocketSession> websocketSessionMap;

	public ConsumerWebSocketSessionService() {
		websocketSessionMap = new ConcurrentHashMap<>();
	}

	@Override
	public void store(ConsumerId consumerId, WebSocketSession webSocketSession) {
		websocketSessionMap.putIfAbsent(consumerId, webSocketSession);
	}

	@Override
	public void close(ConsumerId consumerId, CloseStatus closeStatus) throws IOException {
		WebSocketSession webSocketSession = websocketSessionMap.get(consumerId);
		if (webSocketSession != null && webSocketSession.isOpen()) {
			webSocketSession.close(closeStatus);
		}
	}

	@Override
	public void delete(ConsumerId consumerId) {
		websocketSessionMap.remove(consumerId);
	}

	@Override
	public WebSocketSession get(ConsumerId consumerId) {
		return websocketSessionMap.get(consumerId);
	}
}
