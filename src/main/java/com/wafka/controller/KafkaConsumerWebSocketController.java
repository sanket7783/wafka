package com.wafka.controller;

import com.wafka.configurer.SpringContext;
import com.wafka.decoder.WebSocketCommandDecoder;
import com.wafka.model.CommandParameters;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.types.CommandName;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/kafka/consumer/ws/v1", decoders = { WebSocketCommandDecoder.class })
public class KafkaConsumerWebSocketController {
	private final Logger logger;

	private final IWebSocketCommandExecutorService iWebSocketCommandExecutorService;

	private Session session;

	public KafkaConsumerWebSocketController() {
		ApplicationContext applicationContext = SpringContext.getApplicationContext();

		logger = applicationContext.getBean(Logger.class);
		iWebSocketCommandExecutorService = applicationContext.getBean(IWebSocketCommandExecutorService.class);
	}

	@OnOpen
	public void onOpenConnection(Session session) {
		this.session = session;
		executeCommand(new CommandParameters(CommandName.SOCKET_CREATED));
	}

	@OnMessage
	public void onMessage(CommandParameters commandParameters) {
		executeCommand(commandParameters);
	}

	@OnClose
	public void onCloseConnection(CloseReason closeReason) {
		String reason = closeReason.getReasonPhrase();
		if (reason == null || reason.isEmpty()) {
			logger.info("Closing WebSocket session {} due to normal reason", session.getId());
		} else {
			logger.info("Closing WebSocket session {} due to reason: {}", session.getId(), reason);
		}
		executeCommand(new CommandParameters(CommandName.STOP_CONSUMER));
	}

	@OnError
	public void onError(Throwable throwable) {
		logger.error("An error occurred: {}", throwable.getMessage());
		executeCommand(new CommandParameters(CommandName.STOP_CONSUMER));
	}

	private void executeCommand(CommandParameters commandParameters) {
		try {
			iWebSocketCommandExecutorService.execute(commandParameters, session);
		} catch (Exception exception) {
			iWebSocketCommandExecutorService.onExecutionError(exception, session);
		}
	}
}
