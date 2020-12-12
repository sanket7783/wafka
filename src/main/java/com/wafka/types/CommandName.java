package com.wafka.types;

public enum CommandName {
	CREATE_CONSUMER("create-consumer"),
	SUBSCRIBE_TOPIC("subscribe-topic"),
	START_CONSUMER_LOOP("start-consumer"),
	STOP_CONSUMER("stop-consumer"),
	COMMIT_SYNC("commit-sync"),
	UNSUBSCRIBE("unsubscribe");

	private final String description;

	CommandName(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}