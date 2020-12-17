package com.wafka.service;

import com.wafka.exception.CommandExecutionException;
import com.wafka.model.CommandParameters;

import javax.websocket.Session;

public interface IWebSocketCommandExecutorService {
	void execute(CommandParameters commandParameters, Session session) throws CommandExecutionException;

	void onExecutionError(Exception exception, Session session);
}
