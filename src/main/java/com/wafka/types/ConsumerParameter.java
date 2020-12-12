package com.wafka.types;

public enum ConsumerParameter {
	KAFKA_CLUSTER_URI("kafkaClusterUri"),
	CONSUMER_ID("consumerId"),
	GROUP_ID("groupId"),
	ENABLE_AUTO_COMMIT("enableAutoCommit"),
	POLL_DURATION("pollDuration"),
	TOPICS("topics"),
	FETCH_MAX_BYTES("fetchMaxBytes"),
	MAX_PARTITION_FETCH_BYTES("maxPartitionFetchBytes");

	private final String description;

	ConsumerParameter(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}