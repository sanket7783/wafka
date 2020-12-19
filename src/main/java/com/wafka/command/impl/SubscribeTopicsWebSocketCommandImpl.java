package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.InvalidTopicListArgumentException;
import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.IResponse;
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
public class SubscribeTopicsWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IResponseFactory iResponseFactory;

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

		String message;
		if (operationStatus == OperationStatus.SUCCESS) {
			message = "Successfully subscribed to topics: " + topics + " for consumer: " + consumerId;
		} else {
			message = "Error while subscribing to topics: " + topics + " for consumer: " + consumerId;
		}

		IResponse iResponse = iResponseFactory.getResponse(consumerId, ResponseType.COMMUNICATION,
				message, operationStatus);

		iWebSocketSenderService.send(session, iResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.SUBSCRIBE_TOPIC;
	}
}
