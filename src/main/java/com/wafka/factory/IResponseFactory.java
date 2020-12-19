package com.wafka.factory;

import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.model.response.IConsumerResponse;
import com.wafka.model.response.IResponse;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;

import java.util.List;
import java.util.Set;

public interface IResponseFactory {
	IConsumerResponse getResponse(ConsumerId consumerId, List<FetchedContent> fetchedContents,
								  OperationStatus operationStatus);

	IConsumerResponse getResponse(ConsumerId consumerId, ResponseType responseType,
								  OperationStatus operationStatus);

	IConsumerResponse getResponse(ConsumerId consumerId, Set<String> subscriptions,
								  OperationStatus operationStatus);

	IResponse getResponse(Set<ConsumerId> consumerIds);
}
