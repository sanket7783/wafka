package com.wafka.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ByteArraySerializer extends JsonSerializer<byte[]> {
	@Override
	public void serialize(byte[] bytes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {

		jsonGenerator.writeStartArray();
		// Use bit masking to leave only the last 8 bits (encode to UTF-8)
		for (byte _byte : bytes) {
			jsonGenerator.writeNumber(_byte & 0XFF);
		}
		jsonGenerator.writeEndArray();
	}
}
