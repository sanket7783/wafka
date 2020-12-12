package com.wafka.command;

import com.wafka.exception.MissingCommandArgumentException;
import com.wafka.model.CommandParameters;
import com.wafka.types.CommandName;

import javax.websocket.Session;

public interface ICommand {
	void execute(CommandParameters commandParameters, Session session) throws MissingCommandArgumentException;

	CommandName getName();
}
