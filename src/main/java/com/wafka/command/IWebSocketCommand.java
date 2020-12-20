package com.wafka.command;

import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.model.CommandParameters;
import org.springframework.web.socket.WebSocketSession;

public interface IWebSocketCommand extends ICommand {
	void execute(CommandParameters commandParameters, WebSocketSession webSocketSession)
			throws MissingCommandArgumentException;
}
