package com.wafka.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.wafka.types.CommandName;

import java.lang.reflect.Type;

public class CommandNameDeserializer implements JsonDeserializer<CommandName> {
	@Override
	public CommandName deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {

		String jsonElementString = jsonElement.getAsString();
		for (CommandName commandName : CommandName.values()) {
			if (commandName.getDescription().equals(jsonElementString)) {
				return commandName;
			}
		}
		throw new JsonParseException("Unsupported command name: " + jsonElementString);
	}
}
