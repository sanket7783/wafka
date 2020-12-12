package com.wafka.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wafka.serializers.ByteArraySerializer;

public class FetchedContentImpl implements IFetchedContent {
	private static final long serialVersionUID = 8739209817573570290L;

	private final String key;

	private final byte[] content;

	private final String topic;

	private final int partition;

	private final long offset;

	public FetchedContentImpl(String key, byte[] content, String topic, int partition, long offset) {
		this.key = key;
		this.content = content;
		this.topic = topic;
		this.partition = partition;
		this.offset = offset;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	@JsonSerialize(using = ByteArraySerializer.class)
	public byte[] getContent() {
		return content;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public int getPartition() {
		return partition;
	}

	@Override
	public long getOffset() {
		return offset;
	}
}
