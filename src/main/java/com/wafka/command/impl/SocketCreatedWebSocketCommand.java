package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.OperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SocketCreatedWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private Logger logger;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Override
	public void execute(CommandParameters commandParameters, WebSocketSession webSocketSession)
			throws MissingCommandArgumentException {

		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(webSocketSession.getId());

		logger.info("Established WebSocket connection. Session id {}. When requested, the consumer will have id {}",
				webSocketSession.getId(), consumerId);

		OperationResponse consumerOperationResponse = new OperationResponse();
		consumerOperationResponse.setConsumerId(consumerId);
		consumerOperationResponse.setResponseType(ResponseType.COMMUNICATION);
		consumerOperationResponse.setOperationStatus(OperationStatus.SUCCESS);

		iConsumerWebSocketSessionService.store(consumerId, webSocketSession);
		iWebSocketSenderService.send(consumerId, consumerOperationResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.SOCKET_CREATED;
	}
}
