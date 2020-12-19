package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.types.CommandName;
import com.wafka.types.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class CommitOffsetWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		iAutoConsumerOperationService.commitSync(consumerId);
	}

	@Override
	public CommandName getName() {
		return CommandName.COMMIT_SYNC;
	}
}
