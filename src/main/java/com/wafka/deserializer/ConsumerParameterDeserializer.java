package com.wafka.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.wafka.types.ConsumerParameter;

import java.lang.reflect.Type;

public class ConsumerParameterDeserializer implements JsonDeserializer<ConsumerParameter> {
	@Override
	public ConsumerParameter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {

		String jsonElementString = jsonElement.getAsString();
		for (ConsumerParameter consumerParameter : ConsumerParameter.values()) {
			if (consumerParameter.getDescription().equals(jsonElementString)) {
				return consumerParameter;
			}
		}
		throw new JsonParseException("Unsupported consumer parameter: " + jsonElementString);
	}
}
