package com.wafka.factory.impl;

import com.wafka.command.ICommand;
import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.NoSuchCommandException;
import com.wafka.factory.ICommandFactory;
import com.wafka.qualifiers.CommandFactoryProtocol;
import com.wafka.types.CommandName;
import com.wafka.types.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CommandFactoryProtocol(Protocol.WEBSOCKET)
public class WebSocketCommandFactory implements ICommandFactory {
	private final List<IWebSocketCommand> iWebSocketCommandList;

	@Autowired
	public WebSocketCommandFactory(List<IWebSocketCommand> iWebSocketCommandList) {
		this.iWebSocketCommandList = iWebSocketCommandList;
	}

	@Override
	public ICommand getCommand(CommandName commandName) {
		for (IWebSocketCommand iWebSocketCommand : iWebSocketCommandList) {
			if (iWebSocketCommand.getName().equals(commandName)) {
				return iWebSocketCommand;
			}
		}
		throw new NoSuchCommandException(commandName);
	}
}
