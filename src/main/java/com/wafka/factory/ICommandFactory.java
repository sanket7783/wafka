package com.wafka.factory;

import com.wafka.command.ICommand;
import com.wafka.types.CommandName;

public interface ICommandFactory {
	ICommand getCommand(CommandName commandName);
}
