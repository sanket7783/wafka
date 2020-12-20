package com.wafka.service;

import com.wafka.model.CommandParameters;

import javax.websocket.Session;

public interface IWebSocketCommandExecutorService {
	void execute(CommandParameters commandParameters, Session session);
}
