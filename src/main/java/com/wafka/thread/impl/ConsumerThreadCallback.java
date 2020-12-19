package com.wafka.thread.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.ConsumerId;
import com.wafka.model.response.IConsumerResponse;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

public class ConsumerThreadCallback implements IConsumerThreadCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThreadCallback.class);

	private final Session session;

	private final IFetchedContentFactory iFetchedContentFactory;

	private final IResponseFactory iResponseFactory;

	private final IWebSocketSenderService iWebSocketSender;

	public ConsumerThreadCallback(
			Session session,
			IFetchedContentFactory iFetchedContentFactory,
			IResponseFactory iResponseFactory,
			IWebSocketSenderService iWebSocketSender) {

		this.session = session;
		this.iFetchedContentFactory = iFetchedContentFactory;
		this.iWebSocketSender = iWebSocketSender;
		this.iResponseFactory = iResponseFactory;
	}

	@Override
	public void onRecordsReceived(ConsumerId consumerId, ConsumerRecords<String, byte[]> consumerRecords) {
		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(consumerId,
				iFetchedContentFactory.getContents(consumerRecords), OperationStatus.SUCCESS
		);

		iWebSocketSender.send(session, iConsumerResponse);
	}

	@Override
	public void onConsumerError(ConsumerId consumerId, Throwable throwable) {
		String exceptionMessage = throwable.getMessage();
		LOGGER.error("An error occurred during consumer loop: {}.", exceptionMessage);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(consumerId, ResponseType.ERROR,
				OperationStatus.FAIL);

		iWebSocketSender.send(session, iConsumerResponse);
	}
}
