package com.wafka.factory.impl;

import com.wafka.factory.IResponseFactory;
import com.wafka.model.*;
import com.wafka.types.ResponseType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ResponseFactory implements IResponseFactory {
	@Override
	public IResponse getResponse(String message, List<IFetchedContent> fetchedContents) {
		FetchDataResponseImpl fetchDataResponseImpl = new FetchDataResponseImpl(fetchedContents);
		fetchDataResponseImpl.setResponseType(ResponseType.INCOMING_DATA);
		fetchDataResponseImpl.setMessage(message);
		return fetchDataResponseImpl;
	}

	@Override
	public IResponse getResponse(ResponseType responseType, String message) {
		DefaultResponseImpl defaultResponse = new DefaultResponseImpl();
		defaultResponse.setMessage(message);
		defaultResponse.setResponseType(responseType);
		return defaultResponse;
	}

	@Override
	public IResponse getResponse(String message, Set<String> subscriptions) {
		SubscriptionsResponseImpl subscriptionsResponse = new SubscriptionsResponseImpl(subscriptions);
		subscriptionsResponse.setResponseType(ResponseType.COMMUNICATION);
		subscriptionsResponse.setMessage(message);
		return subscriptionsResponse;
	}
}
