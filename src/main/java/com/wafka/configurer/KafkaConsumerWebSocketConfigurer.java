package com.wafka.configurer;

import com.wafka.controller.KafkaConsumerWebSocketController;
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
	public KafkaConsumerWebSocketController kafkaConsumerWebSocketController() {
		return new KafkaConsumerWebSocketController();
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
