package com.wafka.exception;

import com.wafka.types.CommandName;

public class NoSuchCommandException extends ApplicationRuntimeException {
    private static final long serialVersionUID = 3741729740527750838L;

    public NoSuchCommandException(String commandName) {
        super("No such command " + commandName);
    }

    public NoSuchCommandException(CommandName commandName) {
        this(commandName.getDescription());
    }
}
