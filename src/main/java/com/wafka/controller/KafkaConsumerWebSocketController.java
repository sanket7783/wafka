package com.wafka.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wafka.deserializer.CommandNameDeserializer;
import com.wafka.deserializer.ConsumerParameterDeserializer;
import com.wafka.model.CommandParameters;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.types.CommandName;
import com.wafka.types.ConsumerParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.lang.reflect.Type;

@Controller
public class KafkaConsumerWebSocketController extends AbstractWebSocketHandler {
	@Autowired
	private IWebSocketCommandExecutorService iWebSocketCommandExecutorService;

	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.SOCKET_CREATED), webSocketSession);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ConsumerParameter.class, new ConsumerParameterDeserializer());
		gsonBuilder.registerTypeAdapter(CommandName.class, new CommandNameDeserializer());

		Gson gson = gsonBuilder.create();
		Type commandParametersType = new TypeToken<CommandParameters>(){}.getType();
		CommandParameters commandParameters = gson.fromJson(textMessage.getPayload(), commandParametersType);

		iWebSocketCommandExecutorService.execute(commandParameters, session);
	}

	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.STOP_CONSUMER), webSocketSession);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		iWebSocketCommandExecutorService.execute(new CommandParameters(CommandName.STOP_CONSUMER), webSocketSession);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
