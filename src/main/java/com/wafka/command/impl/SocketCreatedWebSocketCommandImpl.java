package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class SocketCreatedWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private Logger logger;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Override
	public void execute(CommandParameters commandParameters, Session session) throws MissingCommandArgumentException {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());

		logger.info("Established WebSocket connection. Session id {}. When requested, the consumer will have id {}",
				session.getId(), consumerId);

		IResponse iResponse = iResponseFactory.getResponse(consumerId, ResponseType.COMMUNICATION,
				"Connected", OperationStatus.SUCCESS);

		iWebSocketSenderService.send(session, iResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.SOCKET_CREATED;
	}
}
