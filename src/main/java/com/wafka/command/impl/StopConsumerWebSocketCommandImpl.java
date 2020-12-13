package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.IConsumerId;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

@Component
public class StopConsumerWebSocketCommandImpl implements IWebSocketCommand {
	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(session.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.stop(iConsumerId);

		// This command could be invoked due to a normal WebSocket closing, and so the
		// session could be already closed. In that case do not send the response to the client because
		// the send would fail.

		if (session.isOpen()) {
			IResponse iResponse;
			if (operationStatus == OperationStatus.SUCCESS) {
				iResponse = iResponseFactory.getResponse(ResponseType.COMMUNICATION,
						"Successfully stopped consumer {}" + iConsumerId);
			} else {
				iResponse = iResponseFactory.getResponse(ResponseType.ERROR,
						"Failed to stop consumer {}" + iConsumerId);
			}
			iWebSocketSenderService.send(session, iResponse);
		}

		iConsumerWebSocketSessionService.delete(iConsumerId);
	}

	@Override
	public CommandName getName() {
		return CommandName.STOP_CONSUMER;
	}
}
