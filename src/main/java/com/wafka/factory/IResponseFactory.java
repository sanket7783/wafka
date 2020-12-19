package com.wafka.factory;

import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.model.IResponse;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;

import java.util.List;
import java.util.Set;

public interface IResponseFactory {
	IResponse getResponse(ConsumerId consumerId, String message, List<FetchedContent> fetchedContents,
						  OperationStatus operationStatus);

	IResponse getResponse(ConsumerId consumerId, ResponseType responseType, String message,
						  OperationStatus operationStatus);

	IResponse getResponse(ConsumerId consumerId, String message, Set<String> subscriptions,
						  OperationStatus operationStatus);
}
