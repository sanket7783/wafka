package com.wafka.factory;

import com.wafka.model.ConsumerThreadSettings;
import com.wafka.thread.AbstractConsumerThread;

public interface IConsumerThreadFactory {
	AbstractConsumerThread getThread(ConsumerThreadSettings consumerThreadSettings);
}
