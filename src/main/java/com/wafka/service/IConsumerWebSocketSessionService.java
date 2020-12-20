package com.wafka.service;

import com.wafka.model.ConsumerId;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface IConsumerWebSocketSessionService {
	void store(ConsumerId consumerId, WebSocketSession session);

	void close(ConsumerId consumerId, CloseStatus closeStatus) throws IOException;

	void delete(ConsumerId consumerId);

	WebSocketSession get(ConsumerId consumerId);
}
