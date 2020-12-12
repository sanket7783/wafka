package com.wafka.factory;

import com.wafka.model.IFetchedContent;
import com.wafka.model.IResponse;
import com.wafka.types.ResponseType;

import java.util.List;

public interface IResponseFactory {
	IResponse getResponse(ResponseType responseType, String message, List<IFetchedContent> fetchedContents);

	IResponse getResponse(ResponseType responseType, String message);
}
