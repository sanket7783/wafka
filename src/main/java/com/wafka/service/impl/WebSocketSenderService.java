package com.wafka.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.model.response.IConsumerResponse;
import com.wafka.service.IWebSocketSenderService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

@Service
public class WebSocketSenderService implements IWebSocketSenderService {
	private final Logger logger;

	private final ObjectMapper objectMapper;

	@Autowired
	public WebSocketSenderService(Logger logger, ObjectMapper objectMapper) {
		this.logger = logger;
		this.objectMapper = objectMapper;
	}

	@Override
	public void send(Session session, IConsumerResponse iConsumerResponse) {
		try {
			session.getBasicRemote().sendBinary(
					ByteBuffer.wrap(objectMapper.writeValueAsBytes(iConsumerResponse))
			);

		} catch (IOException exception) {
			logger.error("Cannot send content to consumer {}: {}", session.getId(), exception.getMessage());
		}
	}
}
