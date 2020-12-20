package com.wafka.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.IResponse;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.service.IWebSocketSenderService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class WebSocketSenderService implements IWebSocketSenderService {
	private final Logger logger;

	private final ObjectMapper objectMapper;

	private final IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Autowired
	public WebSocketSenderService(Logger logger, ObjectMapper objectMapper,
								  IConsumerWebSocketSessionService iConsumerWebSocketSessionService) {
		this.logger = logger;
		this.objectMapper = objectMapper;
		this.iConsumerWebSocketSessionService = iConsumerWebSocketSessionService;
	}

	@Override
	public void send(ConsumerId consumerId, IResponse iResponse) {
		WebSocketSession webSocketSession = iConsumerWebSocketSessionService.get(consumerId);
		try {
			TextMessage textMessage = new TextMessage(objectMapper.writeValueAsBytes(iResponse));
			webSocketSession.sendMessage(textMessage);

		} catch (IOException exception) {
			logger.error("Cannot send content to consumer {}: {}", webSocketSession, exception.getMessage());
		}
	}
}
