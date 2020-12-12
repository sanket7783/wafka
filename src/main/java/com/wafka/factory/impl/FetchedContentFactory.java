package com.wafka.factory.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.FetchedContentImpl;
import com.wafka.model.IFetchedContent;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class FetchedContentFactory implements IFetchedContentFactory {
	@Override
	public List<IFetchedContent> getContents(ConsumerRecords<String, byte[]> consumerRecords) {
		List<IFetchedContent> topicContentList = new LinkedList<>();

		consumerRecords.forEach(consumerRecord -> {
			IFetchedContent iFetchedContent = new FetchedContentImpl(
					consumerRecord.key(), consumerRecord.value(),
					consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset()
			);
			topicContentList.add(iFetchedContent);
		});

		return topicContentList;
	}
}
