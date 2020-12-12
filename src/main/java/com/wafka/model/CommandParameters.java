package com.wafka.model;

import com.wafka.types.CommandName;
import com.wafka.types.ConsumerParameter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CommandParameters {
	private final CommandName commandName;

	private final Map<ConsumerParameter, Object> arguments;

	public CommandParameters(CommandName commandName) {
		this(commandName, new EnumMap<>(ConsumerParameter.class));
	}

	public CommandParameters(CommandName commandName, Map<ConsumerParameter, Object> arguments) {
		this.commandName = commandName;
		this.arguments = Collections.unmodifiableMap(arguments);
	}

	public CommandName getCommandName() {
		return commandName;
	}

	public Optional<Object> getArgument(ConsumerParameter argumentKey) {
		if (!arguments.containsKey(argumentKey)) {
			return Optional.empty();
		}
		return Optional.of(arguments.get(argumentKey));
	}

	public Map<ConsumerParameter, Object> getArguments() {
		return arguments;
	}
}
