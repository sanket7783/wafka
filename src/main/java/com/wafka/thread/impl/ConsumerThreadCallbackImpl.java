package com.wafka.thread.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.IConsumerId;
import com.wafka.model.IResponse;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.types.ResponseType;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

public class ConsumerThreadCallbackImpl implements IConsumerThreadCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThreadCallbackImpl.class);

	private final Session session;

	private final IFetchedContentFactory iFetchedContentFactory;

	private final IResponseFactory iResponseFactory;

	private final IWebSocketSenderService iWebSocketSender;

	public ConsumerThreadCallbackImpl(
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
	public void onRecordsReceived(IConsumerId iConsumerId, ConsumerRecords<String, byte[]> consumerRecords) {
		IResponse iResponse = iResponseFactory.getResponse(iConsumerId, "Successfully fetched data",
				iFetchedContentFactory.getContents(consumerRecords)
		);

		iWebSocketSender.send(session, iResponse);
	}

	@Override
	public void onConsumerError(IConsumerId iConsumerId, Throwable throwable) {
		String exceptionMessage = throwable.getMessage();
		LOGGER.error("An error occurred during consumer loop: {}.", exceptionMessage);

		IResponse iResponse = iResponseFactory.getResponse(iConsumerId, ResponseType.ERROR, exceptionMessage);
		iWebSocketSender.send(session, iResponse);
	}
}
