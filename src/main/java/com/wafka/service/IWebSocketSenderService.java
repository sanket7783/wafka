package com.wafka.service;

import com.wafka.model.ConsumerId;
import com.wafka.model.response.IResponse;

public interface IWebSocketSenderService {
	void send(ConsumerId consumerId, IResponse iResponse);
}
