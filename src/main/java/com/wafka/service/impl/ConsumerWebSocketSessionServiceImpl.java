package com.wafka.service.impl;

import com.wafka.model.IConsumerId;
import com.wafka.service.IConsumerWebSocketSessionService;
import org.springframework.stereotype.Service;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsumerWebSocketSessionServiceImpl implements IConsumerWebSocketSessionService {
	private final Map<IConsumerId, Session> websocketSessionMap;

	public ConsumerWebSocketSessionServiceImpl() {
		websocketSessionMap = new ConcurrentHashMap<>();
	}

	@Override
	public void store(IConsumerId iConsumerId, Session session) {
		websocketSessionMap.putIfAbsent(iConsumerId, session);
	}

	@Override
	public void close(IConsumerId iConsumerId, CloseReason closeReason) throws IOException {
		Session session = websocketSessionMap.get(iConsumerId);
		if (session != null && session.isOpen()) {
			session.close(closeReason);
		}
	}

	@Override
	public void delete(IConsumerId iConsumerId) {
		websocketSessionMap.remove(iConsumerId);
	}
}
