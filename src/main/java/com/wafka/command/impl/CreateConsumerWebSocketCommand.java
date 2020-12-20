package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.MissingConsumerSettingException;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.ConsumerThreadSettings;
import com.wafka.model.response.CreatedConsumerOperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IConsumerService;
import com.wafka.service.IConsumerThreadService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.thread.impl.ConsumerThreadCallback;
import com.wafka.types.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@Component
public class CreateConsumerWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private Logger logger;

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

	@Override
	public void execute(CommandParameters commandParameters, WebSocketSession webSocketSession) {
		Map<ConsumerParameter, Object> consumerParameterMap = commandParameters.getArguments();

		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(webSocketSession.getId());

		// Create physical consumer.
		Properties consumerProperties = iConsumerPropertyFactory.getProperties(consumerParameterMap);
		iConsumerService.create(consumerId, consumerProperties);

		// Create consumer thread (only creation, the thread will not be started)
		KafkaConsumer<String, byte[]> kafkaConsumer = iConsumerService.getConsumerOrThrow(consumerId);

		IConsumerThreadCallback iWebSocketConsumerCallback = new ConsumerThreadCallback(
				iFetchedContentFactory, iWebSocketSenderService);

		ConsumerThreadSettings consumerThreadSettings = new ConsumerThreadSettings();
		consumerThreadSettings.setiWebSocketConsumerCallback(iWebSocketConsumerCallback);
		consumerThreadSettings.setiConsumerIdentifier(consumerId);
		consumerThreadSettings.setWrappedConsumer(kafkaConsumer);

		Object pollDuration = consumerParameterMap.get(ConsumerParameter.POLL_DURATION);
		if (pollDuration == null) {
			throw new MissingConsumerSettingException(ConsumerParameter.POLL_DURATION);
		}

		int pollDurationSeconds = (int)Double.parseDouble(pollDuration.toString());
		consumerParameterMap.put(ConsumerParameter.POLL_DURATION, pollDurationSeconds);
		consumerThreadSettings.setPollLoopDuration(Duration.ofSeconds(pollDurationSeconds));

		iConsumerThreadService.create(consumerThreadSettings);

		CreatedConsumerOperationResponse createdConsumerOperationResponse = new CreatedConsumerOperationResponse();
		createdConsumerOperationResponse.setConsumerParameters(consumerParameterMap);
		createdConsumerOperationResponse.setConsumerId(consumerId);
		createdConsumerOperationResponse.setOperationStatus(OperationStatus.SUCCESS);
		createdConsumerOperationResponse.setResponseType(ResponseType.COMMUNICATION);

		logger.info("Created consumer with settings {}", consumerParameterMap);
		iWebSocketSenderService.send(consumerId, createdConsumerOperationResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.CREATE_CONSUMER;
	}
}
