package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.InvalidTopicListArgumentException;
import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.SubscribeTopicOperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class SubscribeTopicsWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		Optional<Object> topicOptional = commandParameters.getArgument(ConsumerParameter.TOPICS);
		if (!topicOptional.isPresent()) {
			throw new MissingCommandArgumentException(ConsumerParameter.TOPICS, this.getName());
		}

		Set<String> topics;
		try {
			topics = new HashSet<>((List<String>)topicOptional.get());
		} catch (ClassCastException exception) {
			throw new InvalidTopicListArgumentException();
		}

		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.subscribe(consumerId, topics);

		SubscribeTopicOperationResponse subscriptionsConsumerResponse = new SubscribeTopicOperationResponse();
		subscriptionsConsumerResponse.setConsumerId(consumerId);
		subscriptionsConsumerResponse.setSubscriptions(topics);
		subscriptionsConsumerResponse.setResponseType(ResponseType.COMMUNICATION);
		subscriptionsConsumerResponse.setOperationStatus(operationStatus);

		iWebSocketSenderService.send(session, subscriptionsConsumerResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.SUBSCRIBE_TOPIC;
	}
}
