package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.IConsumerId;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Set;

@Component
public class ListSubscriptionsWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(session.getId());
		Set<String> subscriptions = iAutoConsumerOperationService.getSubscriptions(iConsumerId);

		IResponse iResponse = iResponseFactory.getResponse(
				"Succesfully fetched consumer topics.", subscriptions
		);

		iWebSocketSenderService.send(session, iResponse);
	}

	@Override
	public CommandName getName() {
		return CommandName.LIST_SUBSCRIPTIONS;
	}
}
