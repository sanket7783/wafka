package com.wafka.service;

import com.wafka.model.IConsumerId;
import com.wafka.model.IFetchedContent;

import java.util.List;

public interface IManualConsumerOperationService extends IConsumerOperationService{
	List<IFetchedContent> fetch(IConsumerId iConsumerId, int pollDuration);
}
