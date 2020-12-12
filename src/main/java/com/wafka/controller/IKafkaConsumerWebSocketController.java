package com.wafka.controller;

import com.wafka.model.CommandParameters;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public interface IKafkaConsumerWebSocketController {
	void onOpenConnection(Session session);

	void onMessage(CommandParameters commandParameters);

	void onCloseConnection(CloseReason closeReason);

	void onError(Throwable throwable);
}
