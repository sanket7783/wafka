package com.wafka.exception;

import com.wafka.types.CommandName;
import com.wafka.types.ConsumerParameter;

public class MissingCommandArgumentException extends ApplicationRuntimeException {
    private static final long serialVersionUID = 3219189412742161375L;

    public MissingCommandArgumentException(ConsumerParameter consumerParameterKey, CommandName commandName) {
        super("Missing required argument " + consumerParameterKey + " in command " + commandName);
    }
}
