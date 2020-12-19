package com.wafka.factory.impl;

import com.wafka.factory.IResponseFactory;
import com.wafka.model.*;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ResponseFactory implements IResponseFactory {
	@Override
	public IResponse getResponse(ConsumerId consumerId, String message, List<FetchedContent> fetchedContents,
								 OperationStatus operationStatus) {

		FetchDataResponse fetchDataResponseImpl = new FetchDataResponse(fetchedContents);
		fetchDataResponseImpl.setResponseType(ResponseType.INCOMING_DATA);
		fetchDataResponseImpl.setMessage(message);
		fetchDataResponseImpl.setConsumerId(consumerId);
		fetchDataResponseImpl.setOperationStatus(operationStatus);
		return fetchDataResponseImpl;
	}

	@Override
	public IResponse getResponse(ConsumerId consumerId, ResponseType responseType,
								 String message,  OperationStatus operationStatus) {

		DefaultResponse defaultResponse = new DefaultResponse();
		defaultResponse.setMessage(message);
		defaultResponse.setResponseType(responseType);
		defaultResponse.setConsumerId(consumerId);
		defaultResponse.setOperationStatus(operationStatus);
		return defaultResponse;
	}

	@Override
	public IResponse getResponse(ConsumerId consumerId, String message, Set<String> subscriptions,
								 OperationStatus operationStatus) {

		SubscriptionsResponse subscriptionsResponse = new SubscriptionsResponse(subscriptions);
		subscriptionsResponse.setResponseType(ResponseType.COMMUNICATION);
		subscriptionsResponse.setMessage(message);
		subscriptionsResponse.setConsumerId(consumerId);
		subscriptionsResponse.setOperationStatus(operationStatus);
		return subscriptionsResponse;
	}
}
