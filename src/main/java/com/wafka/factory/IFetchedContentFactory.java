package com.wafka.factory;

import com.wafka.model.FetchedContent;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.List;

public interface IFetchedContentFactory {
	List<FetchedContent> getContents(ConsumerRecords<String, byte[]> consumerRecords);
}
