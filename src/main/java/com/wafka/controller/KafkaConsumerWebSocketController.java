package com.wafka.controller;

import com.wafka.configurer.SpringContext;
import com.wafka.decoder.WebSocketCommandDecoder;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.IResponse;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.ResponseType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.text.MessageFormat;

@Component
@ServerEndpoint(value = "/kafka/consumer/ws/v1", decoders = { WebSocketCommandDecoder.class })
public class KafkaConsumerWebSocketController implements IKafkaConsumerWebSocketController {
	private static Logger logger;

	private static IWebSocketCommandExecutorService iWebSocketCommandExecutorService;

	private static IWebSocketSenderService iWebSocketSender;

	private static IResponseFactory iResponseFactory;

	private Session session;

	public KafkaConsumerWebSocketController() { }

	@Autowired
	public KafkaConsumerWebSocketController(
			Logger logger, IWebSocketCommandExecutorService iWebSocketCommandExecutorService,
			IWebSocketSenderService iWebSocketSender, IResponseFactory iResponseFactory ) {

		KafkaConsumerWebSocketController.logger = logger;
		KafkaConsumerWebSocketController.iWebSocketCommandExecutorService = iWebSocketCommandExecutorService;
		KafkaConsumerWebSocketController.iWebSocketSender = iWebSocketSender;
		KafkaConsumerWebSocketController.iResponseFactory = iResponseFactory;
	}

	@OnOpen
	public void onOpenConnection(Session session) {
		this.session = session;
		logger.info("Established WebSocket connection for consumer: {}", session.getId());

		IResponse iResponse = iResponseFactory.getResponse(ResponseType.COMMUNICATION, "Connected");
		iWebSocketSender.send(session, iResponse);
	}

	@OnMessage
	public void onMessage(CommandParameters commandParameters) {
		executeCommand(commandParameters);
	}

	@OnClose
	public void onCloseConnection(CloseReason closeReason) {
		String reason = closeReason.getReasonPhrase();
		if (reason == null || reason.isEmpty()) {
			reason = "close request";
		}
		logger.info("Closing WebSocket session {} due to reason '{}'", session.getId(), reason);
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
			String errorMessage = MessageFormat.format(
					"Error in command execution for consumer: {0}", session.getId());

			logger.error("{}. Exception: {}", errorMessage, exception.getMessage());

			IResponse iResponse = iResponseFactory.getResponse(ResponseType.ERROR, errorMessage);
			iWebSocketSender.send(session, iResponse);
		}
	}
}
