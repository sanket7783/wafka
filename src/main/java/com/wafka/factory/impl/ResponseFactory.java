package com.wafka.factory.impl;

import com.wafka.factory.IResponseFactory;
import com.wafka.model.*;
import com.wafka.model.response.*;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ResponseFactory implements IResponseFactory {
	@Override
	public IConsumerResponse getResponse(ConsumerId consumerId, List<FetchedContent> fetchedContents,
										 OperationStatus operationStatus) {

		FetchDataConsumerResponse fetchDataResponseImpl = new FetchDataConsumerResponse(fetchedContents);
		fetchDataResponseImpl.setResponseType(ResponseType.INCOMING_DATA);
		fetchDataResponseImpl.setConsumerId(consumerId);
		fetchDataResponseImpl.setOperationStatus(operationStatus);
		return fetchDataResponseImpl;
	}

	@Override
	public IConsumerResponse getResponse(ConsumerId consumerId, ResponseType responseType,
										 OperationStatus operationStatus) {

		ConsumerResponse defaultResponse = new ConsumerResponse();
		defaultResponse.setResponseType(responseType);
		defaultResponse.setConsumerId(consumerId);
		defaultResponse.setOperationStatus(operationStatus);
		return defaultResponse;
	}

	@Override
	public IConsumerResponse getResponse(ConsumerId consumerId, Set<String> subscriptions,
										 OperationStatus operationStatus) {

		SubscriptionsConsumerResponse subscriptionsResponse = new SubscriptionsConsumerResponse(subscriptions);
		subscriptionsResponse.setResponseType(ResponseType.COMMUNICATION);
		subscriptionsResponse.setConsumerId(consumerId);
		subscriptionsResponse.setOperationStatus(operationStatus);
		return subscriptionsResponse;
	}

	@Override
	public IResponse getResponse(Set<ConsumerId> consumerIds) {
		RegisteredConsumersResponse registeredConsumersResponse = new RegisteredConsumersResponse();
		registeredConsumersResponse.setConsumers(consumerIds);
		registeredConsumersResponse.setResponseType(ResponseType.COMMUNICATION);
		return registeredConsumersResponse;
	}
}
