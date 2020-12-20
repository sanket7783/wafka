package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.SubscribeTopicOperationResponse;
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

import java.util.Set;

@Component
public class ListSubscriptionsWebSocketCommand implements IWebSocketCommand {
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
		Set<String> subscriptions = iAutoConsumerOperationService.getSubscriptions(consumerId);

		SubscribeTopicOperationResponse subscriptionsConsumerResponse = new SubscribeTopicOperationResponse();
		subscriptionsConsumerResponse.setConsumerId(consumerId);
		subscriptionsConsumerResponse.setSubscriptions(subscriptions);
		subscriptionsConsumerResponse.setResponseType(ResponseType.COMMUNICATION);
		subscriptionsConsumerResponse.setOperationStatus(OperationStatus.SUCCESS);

		iWebSocketSenderService.send(consumerId, subscriptionsConsumerResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.LIST_SUBSCRIPTIONS;
	}
}
