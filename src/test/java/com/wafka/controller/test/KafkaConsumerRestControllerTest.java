package com.wafka.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.application.WafkaApplication;
import com.wafka.controller.KafkaConsumerRestController;
import com.wafka.model.response.ConsumerResponse;
import com.wafka.model.response.FetchDataConsumerResponse;
import com.wafka.model.response.RegisteredConsumersResponse;
import com.wafka.model.response.SubscriptionsConsumerResponse;
import com.wafka.types.OperationStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = WafkaApplication.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = KafkaConsumerRestController.class
)
public class KafkaConsumerRestControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerRestControllerTest.class);
	private static final String basePath = "/kafka/consumer/rest/v1";
	private static final String testConsumerId = "testConsumerId";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testA_ConsumerCreatedSuccessfully() throws Exception {
		logger.info("Running testA_ConsumerCreatedSuccessfully");

		Map<String, Object> consumerCreationResponse = mockCreateConsumerCreationOperation();
		Assert.notEmpty(consumerCreationResponse, "Consumer creation map is empty!");
	}

	@Test
	public void testB_RegisteredConsumersListNotEmpty() throws Exception {
		logger.info("Running testB_RegisteredConsumersListNotEmpty");

		RegisteredConsumersResponse consumerListResponse = mockRegisteredConsumerListOperation();
		Assert.notEmpty(consumerListResponse.getConsumers(), "Consumers list mut not be empty!");
	}

	@Test
	public void testC_ConsumerSubscribeToTopics() throws Exception {
		logger.info("Running testC_ConsumerSubscribeToTopics");

		SubscriptionsConsumerResponse subscriptionsResponse = mockConsumerTopicSubscribeOperation();
		Assert.notEmpty(subscriptionsResponse.getSubscriptions(), "Consumer subscription list is empty!");

		Assert.isTrue(subscriptionsResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Subscribe operation failed!");
	}

	@Test
	public void testD_ConsumerSubscriptionListNotEmpty() throws Exception {
		logger.info("Running testD_ConsumerSubscriptionListNotEmpty");

		SubscriptionsConsumerResponse subscriptionListResponse = mockConsumerSubscriptionListOperation();
		Assert.notEmpty(subscriptionListResponse.getSubscriptions(), "Consumer subscription list is empty!");

		Assert.isTrue(subscriptionListResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Subscription list fetch operation failed!");
	}

	@Test
	public void testE_ConsumerFetchEmptyData() throws Exception {
		logger.info("Running testE_ConsumerFetchEmptyData");

		FetchDataConsumerResponse fetchDataResponse = mockConsumerFetchDataOperation();
		Assert.isTrue(fetchDataResponse.getFetchedContents().isEmpty(), "Fetched data is not empty");

		Assert.isTrue(fetchDataResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Fetch consumer data operation failed!");
	}

	@Test
	public void testF_ConsumerUnsubscribeFromTopics() throws Exception {
		logger.info("Running testF_ConsumerUnsubscribeFromTopics");

		ConsumerResponse unsubscribeResponse = mockConsumerUnsubscribeOperation();
		Assert.isTrue(unsubscribeResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Unsubscribe operation failed!");
	}

	@Test
	public void testG_ConsumerStop() throws Exception {
		logger.info("Running testG_ConsumerStop");

		ConsumerResponse stopResponse = mockConsumerStopOperation();
		Assert.isTrue(stopResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Stop operation failed!");
	}

	private RegisteredConsumersResponse mockRegisteredConsumerListOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
				MockMvcRequestBuilders.get(basePath + "/list");

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, RegisteredConsumersResponse.class);
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

	private SubscriptionsConsumerResponse mockConsumerTopicSubscribeOperation() throws Exception {
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

		return readMvcResultAs(mvcResult, SubscriptionsConsumerResponse.class);
	}

	private FetchDataConsumerResponse mockConsumerFetchDataOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/fetch", testConsumerId)
				.param("pollDuration", String.valueOf(1));

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, FetchDataConsumerResponse.class);
	}

	private SubscriptionsConsumerResponse mockConsumerSubscriptionListOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/subscriptions", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, SubscriptionsConsumerResponse.class);
	}

	private ConsumerResponse mockConsumerUnsubscribeOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/unsubscribe", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, ConsumerResponse.class);
	}

	private ConsumerResponse mockConsumerStopOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/stop", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, ConsumerResponse.class);
	}

	@SuppressWarnings("unchecked cast")
	private Map<String, Object> readMvcResultAsMap(MvcResult mvcResult)
			throws UnsupportedEncodingException, JsonProcessingException {

		return readMvcResultAs(mvcResult, Map.class);
	}

	private <T> T readMvcResultAs(MvcResult mvcResult, Class<T> clazz)
			throws UnsupportedEncodingException, JsonProcessingException {

		String responseContent = mvcResult.getResponse().getContentAsString();
		return objectMapper.readValue(responseContent, clazz);
	}
}
