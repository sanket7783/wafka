package com.wafka.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.application.WafkaApplication;
import com.wafka.controller.KafkaConsumerRestController;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = WafkaApplication.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = KafkaConsumerRestController.class
)
public class KafkaConsumerRestControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String basePath = "/kafka/consumer/rest/v1";
	private static final String testConsumerId = "testConsumerId";

	@Test
	@Order(1)
	public void testListConsumerIsEmpty() throws Exception {
		Map<String ,Object> resultMap = mockConsumerListOperation();

		Assert.isTrue(((ArrayList<?>)resultMap.get("consumers")).isEmpty(),
				"Consumers list must be empty!");
	}

	@Test
	@Order(2)
	public void testConsumerCreatedSuccessfully() throws Exception {
		Map<String, Object> consumerCreationResponse = mockCreateConsumerCreationOperation();
		Assert.notEmpty(consumerCreationResponse, "Consumer creation map is empty!");

		Map<String ,Object> consumerListResponse = mockConsumerListOperation();
		Assert.notEmpty((ArrayList<?>)consumerListResponse.get("consumers"),
				"Consumers list mut not be empty!");
	}

	@Test
	@Order(3)
	public void testConsumerSubscribeToTopics() throws Exception {
		Map<String, Object> consumerTopicSubscriptionResponse = mockConsumerTopicSubscriptionOperation();
		Assert.notEmpty(consumerTopicSubscriptionResponse, "Consumer topic subscription map is empty!");
	}

	private Map<String, Object> mockConsumerListOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
				MockMvcRequestBuilders.get(basePath + "/list");

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAsMap(mvcResult);
	}

	private Map<String, Object> mockCreateConsumerCreationOperation() throws Exception {
		String testConsumerGroupId = "testConsumerGroupId";
		String enableAutoCommit = "true";
		String kafkaClusterId = "localhost:9092";

		// Create a consumer using those parameters.
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post(basePath + "/{consumerId}/{groupId}/create", testConsumerId, testConsumerGroupId)
				.param("enableAutoCommit", enableAutoCommit)
				.param("kafkaClusterId", kafkaClusterId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated())
				.andReturn();

		return readMvcResultAsMap(mvcResult);
	}

	private Map<String, Object> mockConsumerTopicSubscriptionOperation() throws Exception {
		List<String> topics = Collections.singletonList("testing-topic");
		String topicJsonString = objectMapper.writeValueAsString(topics);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post(basePath + "/{consumerId}/subscribe", testConsumerId)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(topicJsonString);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAsMap(mvcResult);
	}

	@SuppressWarnings("unchecked cast")
	private Map<String, Object> readMvcResultAsMap(MvcResult mvcResult)
			throws UnsupportedEncodingException, JsonProcessingException {

		String responseContent = mvcResult.getResponse().getContentAsString();
		return objectMapper.readValue(responseContent, Map.class);
	}
}
