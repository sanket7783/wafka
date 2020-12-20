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

	public KafkaConsumerWebSocketController() {
		ApplicationContext applicationContext = SpringContext.getApplicationContext();

		logger = applicationContext.getBean(Logger.class);
		iWebSocketCommandExecutorService = applicationContext.getBean(IWebSocketCommandExecutorService.class);
	}

	@OnOpen
	public void onOpenConnection(Session session) {
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.SOCKET_CREATED), session);
	}

	@OnMessage
	public void onMessage(CommandParameters commandParameters, Session session) {
		iWebSocketCommandExecutorService.execute(commandParameters, session);
	}

	@OnClose
	public void onCloseConnection(Session session, CloseReason closeReason) {
		String reason = closeReason.getReasonPhrase();
		if (reason == null || reason.isEmpty()) {
			logger.info("Closing WebSocket session {} due to normal reason", session.getId());
		} else {
			logger.info("Closing WebSocket session {} due to reason: {}", session.getId(), reason);
		}
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.STOP_CONSUMER), session);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("An error occurred: {}", throwable.getMessage());
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.STOP_CONSUMER), session);
	}
}
