package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.OperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StartConsumerWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Override
	public void execute(CommandParameters commandParameters, WebSocketSession webSocketSession) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(webSocketSession.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.start(consumerId);

		OperationResponse consumerOperationResponse = new OperationResponse();
		consumerOperationResponse.setConsumerId(consumerId);
		consumerOperationResponse.setResponseType(ResponseType.COMMUNICATION);
		consumerOperationResponse.setOperationStatus(operationStatus);

		iWebSocketSenderService.send(consumerId, consumerOperationResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.START_CONSUMER_LOOP;
	}
}
