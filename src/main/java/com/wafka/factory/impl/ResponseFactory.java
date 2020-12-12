package com.wafka.factory.impl;

import com.wafka.factory.IResponseFactory;
import com.wafka.model.IFetchedContent;
import com.wafka.model.IResponse;
import com.wafka.model.ResponseImpl;
import com.wafka.types.ResponseType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseFactory implements IResponseFactory {
	@Override
	public IResponse getResponse(ResponseType responseType, String message, List<IFetchedContent> fetchedContents) {
		IResponse iResponse = new ResponseImpl();
		iResponse.setMessage(message);
		iResponse.setResponseType(responseType);
		iResponse.setFetchedContents(fetchedContents);
		return iResponse;
	}

	@Override
	public IResponse getResponse(ResponseType responseType, String message) {
		return getResponse(responseType, message, new ArrayList<>());
	}
}
