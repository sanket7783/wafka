package com.wafka.service;

import com.wafka.model.response.IConsumerResponse;

import javax.websocket.Session;

public interface IWebSocketSenderService {
	void send(Session session, IConsumerResponse iConsumerResponse);
}
