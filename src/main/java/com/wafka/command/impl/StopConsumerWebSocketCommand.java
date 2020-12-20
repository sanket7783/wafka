package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.types.CommandName;
import com.wafka.types.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StopConsumerWebSocketCommand implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Override
	public void execute(CommandParameters commandParameters, WebSocketSession webSocketSession) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(webSocketSession.getId());
		iAutoConsumerOperationService.stop(consumerId);
		iConsumerWebSocketSessionService.delete(consumerId);
	}

	@Override
	public CommandName getName() {
		return CommandName.STOP_CONSUMER;
	}
}
