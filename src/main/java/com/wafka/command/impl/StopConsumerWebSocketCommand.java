package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.OperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class StopConsumerWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.stop(consumerId);

		// This command could be invoked due to a normal WebSocket closing, and so the
		// session could be already closed. In that case do not send the response to the client because
		// the send would fail.

		if (session.isOpen()) {
			OperationResponse consumerOperationResponse = new OperationResponse();
			consumerOperationResponse.setConsumerId(consumerId);
			consumerOperationResponse.setResponseType(ResponseType.COMMUNICATION);
			consumerOperationResponse.setOperationStatus(operationStatus);

			iWebSocketSenderService.send(session, consumerOperationResponse);
		}

		iConsumerWebSocketSessionService.delete(consumerId);
	}

	@Override
	public CommandName getName() {
		return CommandName.STOP_CONSUMER;
	}
}
