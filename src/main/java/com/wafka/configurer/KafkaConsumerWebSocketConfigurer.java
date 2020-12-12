package com.wafka.configurer;

import com.wafka.controller.IKafkaConsumerWebSocketController;
import com.wafka.controller.KafkaConsumerWebSocketController;
import com.wafka.factory.IResponseFactory;
import com.wafka.service.IWebSocketCommandExecutorService;
import com.wafka.service.IWebSocketSenderService;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class KafkaConsumerWebSocketConfigurer implements WebSocketConfigurer {
	@Bean
	public IKafkaConsumerWebSocketController iKafkaConsumerWebSocketController(
			Logger logger, IWebSocketCommandExecutorService iWebSocketCommandExecutorService,
			IWebSocketSenderService iWebSocketSender, IResponseFactory iResponseFactory ) {

		return new KafkaConsumerWebSocketController(logger, iWebSocketCommandExecutorService,
				iWebSocketSender, iResponseFactory
		);
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		// No operations here.
	}
}
