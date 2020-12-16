package com.wafka.factory.impl;

import com.wafka.factory.IConsumerThreadFactory;
import com.wafka.model.ConsumerThreadSettings;
import com.wafka.thread.AbstractConsumerThread;
import com.wafka.thread.impl.ConsumerThreadImpl;
import org.springframework.stereotype.Service;

@Service
public class ConsumerThreadFactory implements IConsumerThreadFactory {
	@Override
	public AbstractConsumerThread getThread(ConsumerThreadSettings consumerThreadSettings) {
		ConsumerThreadImpl consumerThread = new ConsumerThreadImpl(consumerThreadSettings);
		consumerThread.setName("WSC-Thread-" + consumerThreadSettings.getiConsumerIdentifier());
		return consumerThread;
	}
}
