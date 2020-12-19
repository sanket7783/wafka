package com.wafka.service;

import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;

import java.util.List;

public interface IManualConsumerOperationService extends IConsumerOperationService{
	List<FetchedContent> fetch(ConsumerId consumerId, int pollDuration);
}
