package com.wafka.factory;

import com.wafka.model.IFetchedContent;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.List;

public interface IFetchedContentFactory {
	List<IFetchedContent> getContents(ConsumerRecords<String, byte[]> consumerRecords);
}
