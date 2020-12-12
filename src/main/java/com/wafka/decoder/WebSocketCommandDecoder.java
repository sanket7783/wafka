package com.wafka.decoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wafka.deserializer.CommandNameDeserializer;
import com.wafka.deserializer.ConsumerParameterDeserializer;
import com.wafka.model.CommandParameters;
import com.wafka.types.CommandName;
import com.wafka.types.ConsumerParameter;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.lang.reflect.Type;

public class WebSocketCommandDecoder implements Decoder.Text<CommandParameters> {
	private static final GsonBuilder gsonBuilder;

	static {
		gsonBuilder = new GsonBuilder();
	}

	@Override
	public CommandParameters decode(String commandToDecode) {
		Gson gson = gsonBuilder.create();
		Type commandParametersType = new TypeToken<CommandParameters>(){}.getType();
		return gson.fromJson(commandToDecode, commandParametersType);
	}

	@Override
	public boolean willDecode(String commandToDecode) {
		return commandToDecode != null && !commandToDecode.trim().isEmpty();
	}

	@Override
	public void init(EndpointConfig endpointConfig) {
		gsonBuilder.registerTypeAdapter(ConsumerParameter.class, new ConsumerParameterDeserializer());
		gsonBuilder.registerTypeAdapter(CommandName.class, new CommandNameDeserializer());
	}

	@Override
	public void destroy() {
		// No destruction required.
	}
}
