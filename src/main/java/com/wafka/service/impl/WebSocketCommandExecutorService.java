package com.wafka.service.impl;

import com.wafka.command.ICommand;
import com.wafka.exception.CommandExecutionException;
import com.wafka.factory.ICommandFactory;
import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.CommandParameters;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.IConsumerResponse;
import com.wafka.qualifiers.CommandFactoryProtocol;
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

import javax.websocket.Session;

@Service
public class WebSocketCommandExecutorService implements IWebSocketCommandExecutorService {
	@Autowired
	private Logger logger;

	@Autowired
	@CommandFactoryProtocol(Protocol.WEBSOCKET)
	private ICommandFactory iCommandFactory;

	@Autowired
	@ConsumerIdProtocol(Protocol.WEBSOCKET)
	private IConsumerIdFactory iConsumerIdFactory;

	@Autowired
	private IResponseFactory iResponseFactory;

	@Autowired
	private IWebSocketSenderService iWebSocketSenderService;

	@Override
	public void execute(CommandParameters commandParameters, Session session) throws CommandExecutionException{
		CommandName commandName = commandParameters.getCommandName();
		try {
			ICommand iCommand = iCommandFactory.getCommand(commandName);
			iCommand.execute(commandParameters, session);

		} catch (Exception exception) {
			logger.error("An error occurred during execution of {}: {}", commandName, exception.getMessage());
			throw new CommandExecutionException(exception);
		}
	}

	@Override
	public void onExecutionError(Exception exception, Session session) {
		ConsumerId consumerId = iConsumerIdFactory.getConsumerId(session.getId());
		logger.error("Exception for consumer {}: {}", consumerId, exception.getMessage());

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(consumerId,
				ResponseType.ERROR, OperationStatus.FAIL);

		iWebSocketSenderService.send(session, iConsumerResponse);
	}
}
