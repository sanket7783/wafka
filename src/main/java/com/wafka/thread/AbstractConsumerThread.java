package com.wafka.thread;

import java.util.Collection;
import java.util.concurrent.Future;

public abstract class AbstractConsumerThread extends Thread {
	public abstract void commitSync();

	public abstract void stopPolling();

	public abstract void updateSubscriptions(Collection<String> topics);

	public abstract void unsubscribe();

	public abstract Future<Object> getSubscriptions();
}
