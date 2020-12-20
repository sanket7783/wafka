package com.wafka.thread.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.model.response.FetchDataOperationResponse;
import com.wafka.model.response.OperationResponse;
import com.wafka.service.IWebSocketSenderService;
import com.wafka.thread.IConsumerThreadCallback;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsumerThreadCallback implements IConsumerThreadCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThreadCallback.class);

	private final IFetchedContentFactory iFetchedContentFactory;

	private final IWebSocketSenderService iWebSocketSenderService;

	public ConsumerThreadCallback(IFetchedContentFactory iFetchedContentFactory,
								  IWebSocketSenderService iWebSocketSenderService) {

		this.iFetchedContentFactory = iFetchedContentFactory;
		this.iWebSocketSenderService = iWebSocketSenderService;
	}

	@Override
	public void onRecordsReceived(ConsumerId consumerId, ConsumerRecords<String, byte[]> consumerRecords) {
		List<FetchedContent> fetchedContentList = iFetchedContentFactory.getContents(consumerRecords);

		FetchDataOperationResponse fetchDataConsumerResponse = new FetchDataOperationResponse(fetchedContentList);
		fetchDataConsumerResponse.setResponseType(ResponseType.INCOMING_DATA);
		fetchDataConsumerResponse.setConsumerId(consumerId);
		fetchDataConsumerResponse.setOperationStatus(OperationStatus.SUCCESS);

		iWebSocketSenderService.send(consumerId, fetchDataConsumerResponse);
	}

	@Override
	public void onConsumerError(ConsumerId consumerId, Throwable throwable) {
		String exceptionMessage = throwable.getMessage();
		LOGGER.error("An error occurred during consumer loop: {}.", exceptionMessage);

		OperationResponse consumerOperationResponse = new OperationResponse();
		consumerOperationResponse.setConsumerId(consumerId);
		consumerOperationResponse.setResponseType(ResponseType.ERROR);
		consumerOperationResponse.setOperationStatus(OperationStatus.FAIL);

		iWebSocketSenderService.send(consumerId, consumerOperationResponse);
	}
}
