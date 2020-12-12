package com.wafka.model;

import com.wafka.model.IConsumerId;
import com.wafka.thread.IConsumerThreadCallback;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

public class ConsumerThreadSettings {
	private IConsumerThreadCallback iWebSocketConsumerCallback;
	private IConsumerId iConsumerId;
	private Duration pollLoopDuration;
	private KafkaConsumer<String, byte[]> wrappedConsumer;

	public ConsumerThreadSettings() {
		this.pollLoopDuration = Duration.ofSeconds(1);
	}

	public IConsumerThreadCallback getiWebSocketConsumerCallback() {
		return iWebSocketConsumerCallback;
	}

	public void setiWebSocketConsumerCallback(IConsumerThreadCallback iWebSocketConsumerCallback) {
		this.iWebSocketConsumerCallback = iWebSocketConsumerCallback;
	}

	public IConsumerId getiConsumerIdentifier() {
		return iConsumerId;
	}

	public void setiConsumerIdentifier(IConsumerId iConsumerId) {
		this.iConsumerId = iConsumerId;
	}

	public Duration getPollLoopDuration() {
		return pollLoopDuration;
	}

	public void setPollLoopDuration(Duration pollLoopDuration) {
		this.pollLoopDuration = pollLoopDuration;
	}

	public KafkaConsumer<String, byte[]> getWrappedConsumer() {
		return wrappedConsumer;
	}

	public void setWrappedConsumer(KafkaConsumer<String, byte[]> wrappedConsumer) {
		this.wrappedConsumer = wrappedConsumer;
	}
}
