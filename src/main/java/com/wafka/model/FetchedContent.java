package com.wafka.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wafka.serializers.ByteArraySerializer;

import java.io.Serializable;

public class FetchedContent implements Serializable {
	private static final long serialVersionUID = -6311943260244362539L;

	private final String key;

	private final byte[] content;

	private final String topic;

	private final int partition;

	private final long offset;

	public FetchedContent(String key, byte[] content, String topic, int partition, long offset) {
		this.key = key;
		this.content = content;
		this.topic = topic;
		this.partition = partition;
		this.offset = offset;
	}

	public String getKey() {
		return key;
	}

	@JsonSerialize(using = ByteArraySerializer.class)
	public byte[] getContent() {
		return content;
	}

	public String getTopic() {
		return topic;
	}

	public int getPartition() {
		return partition;
	}

	public long getOffset() {
		return offset;
	}
}
