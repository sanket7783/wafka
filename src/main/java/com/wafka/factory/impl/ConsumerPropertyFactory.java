package com.wafka.factory.impl;

import com.wafka.exception.MissingConsumerSettingException;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.types.ConsumerParameter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public class ConsumerPropertyFactory implements IConsumerPropertyFactory {
	@Override
	public Properties getProperties(Map<ConsumerParameter, Object> parametersMap) {
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

		Object kafkaClusterUri = parametersMap.get(ConsumerParameter.KAFKA_CLUSTER_URI);
		if (kafkaClusterUri == null) {
			throw new MissingConsumerSettingException(ConsumerParameter.KAFKA_CLUSTER_URI);
		}
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaClusterUri);

		Object groupId = parametersMap.get(ConsumerParameter.GROUP_ID);
		if (groupId == null) {
			throw new MissingConsumerSettingException(ConsumerParameter.GROUP_ID);
		}
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		Object fetchMaxBytesConfig = parametersMap.get(ConsumerParameter.FETCH_MAX_BYTES);
		if (fetchMaxBytesConfig != null) {
			int maxBytes = Integer.parseInt(fetchMaxBytesConfig.toString());
			consumerProperties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, maxBytes);
		}

		Object partitionMaxBytesConfig = parametersMap.get(ConsumerParameter.MAX_PARTITION_FETCH_BYTES);
		if (partitionMaxBytesConfig != null) {
			int partitionMaxBytes = Integer.parseInt(partitionMaxBytesConfig.toString());
			consumerProperties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, partitionMaxBytes);
		}

		Object autoCommit = parametersMap.get(ConsumerParameter.ENABLE_AUTO_COMMIT);
		if (autoCommit != null) {
			boolean autoCommitValue = Boolean.parseBoolean(autoCommit.toString());
			consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommitValue);
		}
		return consumerProperties;
	}
}
