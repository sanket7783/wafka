package com.wafka.service;

import com.wafka.model.ConsumerId;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;

public interface IConsumerWebSocketSessionService {
	void store(ConsumerId consumerId, Session session);

	void close(ConsumerId consumerId, CloseReason closeReason) throws IOException;

	void delete(ConsumerId consumerId);
}
