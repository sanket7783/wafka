package com.wafka.factory.impl;

import com.wafka.factory.IFetchedContentFactory;
import com.wafka.model.FetchedContent;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class FetchedContentFactory implements IFetchedContentFactory {
	@Override
	public List<FetchedContent> getContents(ConsumerRecords<String, byte[]> consumerRecords) {
		List<FetchedContent> topicContentList = new LinkedList<>();

		consumerRecords.forEach(consumerRecord -> {
			FetchedContent fetchedContent = new FetchedContent(
					consumerRecord.key(), consumerRecord.value(),
					consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset()
			);
			topicContentList.add(fetchedContent);
		});

		return topicContentList;
	}
}
