package com.wafka.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.model.IResponse;
import com.wafka.service.IWebSocketSenderService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

@Service
public class WebSocketSenderServiceImpl implements IWebSocketSenderService {
	private final Logger logger;

	private final ObjectMapper objectMapper;

	@Autowired
	public WebSocketSenderServiceImpl(Logger logger, ObjectMapper objectMapper) {
		this.logger = logger;
		this.objectMapper = objectMapper;
	}

	@Override
	public void send(Session session, IResponse iResponse) {
		try {
			session.getBasicRemote().sendBinary(
					ByteBuffer.wrap(objectMapper.writeValueAsBytes(iResponse))
			);

		} catch (IOException exception) {
			logger.error("Cannot send content to consumer {}: {}", session.getId(), exception.getMessage());
		}
	}
}
