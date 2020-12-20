package com.wafka.service.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IWebSocketCommandFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.OperationResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.types.CommandName;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketCommandExecutorService implements IWebSocketCommandExecutorService {
	@Autowired
	private Logger logger;

	@Autowired
	private IWebSocketCommandFactory iWebSocketCommandFactory;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Override
	public void execute(CommandParameters commandParameters, WebSocketSession webSocketSession) {
		CommandName commandName = commandParameters.getCommandName();
		try {
			IWebSocketCommand iWebSocketCommand = iWebSocketCommandFactory.getCommand(commandName);
			iWebSocketCommand.execute(commandParameters, webSocketSession);

		} catch (Exception exception) {
			ConsumerId consumerId = iConsumerIdFactory.getConsumerId(webSocketSession.getId());
			logger.error("Exception for consumer {}: {}", consumerId, exception.getMessage(), exception);

			OperationResponse operationResponse = new OperationResponse();
			operationResponse.setConsumerId(consumerId);
			operationResponse.setResponseType(ResponseType.ERROR);
			operationResponse.setOperationStatus(OperationStatus.FAIL);

			iWebSocketSenderService.send(consumerId, operationResponse);
		}
	}
}
