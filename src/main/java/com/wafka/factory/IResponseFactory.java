package com.wafka.factory;

import com.wafka.model.IConsumerId;
import com.wafka.model.IFetchedContent;
import com.wafka.model.IResponse;
import com.wafka.types.ResponseType;

import java.util.List;
import java.util.Set;

public interface IResponseFactory {
	IResponse getResponse(IConsumerId iConsumerId, String message, List<IFetchedContent> fetchedContents);

	IResponse getResponse(IConsumerId iConsumerId, ResponseType responseType, String message);

	IResponse getResponse(IConsumerId iConsumerId, String message, Set<String> subscriptions);
}
