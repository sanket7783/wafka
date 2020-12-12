package com.wafka.service.impl;

import com.wafka.command.ICommand;
import com.wafka.factory.ICommandFactory;
import com.wafka.model.CommandParameters;
import com.wafka.qualifiers.CommandFactoryProtocol;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.types.CommandName;
import com.wafka.types.Protocol;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

@Service
public class WebSocketCommandExecutorServiceImpl implements IWebSocketCommandExecutorService {
	private final Logger logger;

	private final ICommandFactory iCommandFactory;

	@Autowired
	public WebSocketCommandExecutorServiceImpl(
			Logger logger, @CommandFactoryProtocol(Protocol.WEBSOCKET) ICommandFactory iCommandFactory) {

		this.logger = logger;
		this.iCommandFactory = iCommandFactory;
	}

	@Override
	public void execute(CommandParameters commandParameters, Session session) {
		CommandName commandName = commandParameters.getCommandName();
		try {
			ICommand iCommand = iCommandFactory.getCommand(commandName);
			iCommand.execute(commandParameters, session);

		} catch (Exception exception) {
			logger.error("An error occurred during execution of {}: {}", commandName, exception.getMessage());
			throw exception;
		}
	}
}
