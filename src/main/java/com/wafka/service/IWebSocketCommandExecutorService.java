package com.wafka.service;

import com.wafka.model.CommandParameters;
import org.springframework.web.socket.WebSocketSession;

public interface IWebSocketCommandExecutorService {
	void execute(CommandParameters commandParameters, WebSocketSession webSocketSession);
}
