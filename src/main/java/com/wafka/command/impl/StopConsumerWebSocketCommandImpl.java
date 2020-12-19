package com.wafka.command.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
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
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		OperationStatus operationStatus = iAutoConsumerOperationService.stop(consumerId);

		// This command could be invoked due to a normal WebSocket closing, and so the
		// session could be already closed. In that case do not send the response to the client because
		// the send would fail.

		if (session.isOpen()) {
			String message;
			if (operationStatus == OperationStatus.SUCCESS) {
				message = "Successfully stopped consumer {}" + consumerId;
			} else {
				message = "Failed to stop consumer {}" + consumerId;
			}

			IResponse iResponse = iResponseFactory.getResponse(consumerId, ResponseType.COMMUNICATION,
					message, operationStatus);

			iWebSocketSenderService.send(session, iResponse);
		}

		iConsumerWebSocketSessionService.delete(consumerId);
	}

	@Override
	public CommandName getName() {
		return CommandName.STOP_CONSUMER;
	}
}
