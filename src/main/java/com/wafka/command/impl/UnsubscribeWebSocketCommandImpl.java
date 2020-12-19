package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class UnsubscribeWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.unsubscribe(consumerId);

		String message;
		if (operationStatus == OperationStatus.SUCCESS) {
			message = "Successfully unsubscribed consumer " + consumerId;
		} else {
			message = "Failed to unsubscribe consumer " + consumerId;
		}

		IResponse iResponse = iResponseFactory.getResponse(consumerId, ResponseType.ERROR,
				message, operationStatus);

		iWebSocketSenderService.send(session, iResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.UNSUBSCRIBE;
	}
}
