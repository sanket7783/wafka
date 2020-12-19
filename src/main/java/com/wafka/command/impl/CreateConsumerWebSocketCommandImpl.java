package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.factory.IFetchedContentFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.ConsumerId;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IConsumerService;
import com.wafka.service.IConsumerThreadService;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.thread.impl.ConsumerThreadCallbackImpl;
import com.wafka.types.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@Component
public class CreateConsumerWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IConsumerService iConsumerService;

	@Autowired
	private IConsumerThreadService iConsumerThreadService;

	@Autowired
	private IConsumerPropertyFactory iConsumerPropertyFactory;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IFetchedContentFactory iFetchedContentFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		Map<ConsumerParameter, Object> parametersMap = commandParameters.getArguments();

		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());

		// Create physical consumer.
		Properties consumerProperties = iConsumerPropertyFactory.getProperties(parametersMap);
		iConsumerService.create(consumerId, consumerProperties);

		// Create consumer thread (only creation, the thread will not be started)
		KafkaConsumer<String, byte[]> kafkaConsumer = iConsumerService.getConsumerOrThrow(consumerId);

		IConsumerThreadCallback iWebSocketConsumerCallback = new ConsumerThreadCallbackImpl(
				session, iFetchedContentFactory, iResponseFactory, iWebSocketSenderService
		);

		ConsumerThreadSettings consumerThreadSettings = new ConsumerThreadSettings();
		consumerThreadSettings.setiWebSocketConsumerCallback(iWebSocketConsumerCallback);
		consumerThreadSettings.setiConsumerIdentifier(consumerId);
		consumerThreadSettings.setWrappedConsumer(kafkaConsumer);

		Object pollDuration = parametersMap.get(ConsumerParameter.POLL_DURATION);
		if (pollDuration != null) {
			int pollDurationSeconds = (int)Double.parseDouble(pollDuration.toString());
			consumerThreadSettings.setPollLoopDuration(Duration.ofSeconds(pollDurationSeconds));
		}

		iConsumerThreadService.create(consumerThreadSettings);
		iConsumerWebSocketSessionService.store(consumerId, session);

		IResponse iResponse = iResponseFactory.getResponse(consumerId, ResponseType.COMMUNICATION,
				"Kafka async consumer created with id: " + consumerId, OperationStatus.SUCCESS);

		iWebSocketSenderService.send(session, iResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.CREATE_CONSUMER;
	}
}
