package com.wafka.factory.impl;

import com.wafka.command.IWebSocketCommand;
import com.wafka.exception.NoSuchCommandException;
import com.wafka.factory.IWebSocketCommandFactory;
import com.wafka.types.CommandName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSocketCommandFactory implements IWebSocketCommandFactory {
	private final List<IWebSocketCommand> iWebSocketCommandList;

	@Autowired
	public WebSocketCommandFactory(List<IWebSocketCommand> iWebSocketCommandList) {
		this.iWebSocketCommandList = iWebSocketCommandList;
	}

	@Override
	public IWebSocketCommand getCommand(CommandName commandName) {
		return iWebSocketCommandList.stream()
				.filter(command -> command.getName().equals(commandName))
				.findFirst().orElseThrow(() -> new NoSuchCommandException(commandName));
	}
}
