package com.wafka.service;

import com.wafka.model.IConsumerId;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;

public interface IConsumerWebSocketSessionService {
	void store(IConsumerId iConsumerId, Session session);

	void close(IConsumerId iConsumerId, CloseReason closeReason) throws IOException;

	void delete(IConsumerId iConsumerId);
}
