package com.wafka.model;

import java.io.Serializable;

public interface IFetchedContent extends Serializable {
	String getKey();

	byte[] getContent();

	String getTopic();

	int getPartition();

	long getOffset();
}
