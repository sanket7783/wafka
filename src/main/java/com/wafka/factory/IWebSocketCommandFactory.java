package com.wafka.factory;

import com.wafka.command.IWebSocketCommand;
import com.wafka.types.CommandName;

public interface IWebSocketCommandFactory extends ICommandFactory {
	@Override
	IWebSocketCommand getCommand(CommandName commandName);
}
