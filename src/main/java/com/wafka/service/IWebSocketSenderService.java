package com.wafka.service;

import com.wafka.model.IResponse;

import javax.websocket.Session;

public interface IWebSocketSenderService {
	void send(Session session, IResponse iResponse);
}
