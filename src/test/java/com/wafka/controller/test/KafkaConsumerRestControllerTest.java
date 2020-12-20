package com.wafka.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafka.application.WafkaApplication;
import com.wafka.controller.KafkaConsumerRestController;
import com.wafka.model.response.*;
import com.wafka.types.OperationStatus;
import com.wafka.types.ResponseType;
import org.json.JSONException;
import org.json.JSONObject;
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
		CreatedConsumerOperationResponse createdConsumerOperationResponse = mockCreateConsumerCreationOperation();

		Assert.isTrue(createdConsumerOperationResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Consumer creation map is empty!");

		Assert.notEmpty(createdConsumerOperationResponse.getConsumerParameters(),
				"Consumer parameters map is empty!");

		Assert.isTrue(createdConsumerOperationResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testB_RegisteredConsumersListNotEmpty() throws Exception {
		logger.info("Running testB_RegisteredConsumersListNotEmpty");
		RegisteredConsumersResponse registeredConsumersResponse = mockRegisteredConsumerListOperation();

		Assert.notEmpty(registeredConsumersResponse.getConsumers(),
				"Consumers list mut not be empty!");

		Assert.isTrue(registeredConsumersResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testC_ConsumerSubscribeToTopics() throws Exception {
		logger.info("Running testC_ConsumerSubscribeToTopics");
		SubscribeTopicOperationResponse subscriptionsResponse = mockConsumerTopicSubscribeOperation();

		Assert.notEmpty(subscriptionsResponse.getSubscriptions(),
				"Consumer subscription list is empty!");

		Assert.isTrue(subscriptionsResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Subscribe operation failed!");

		Assert.isTrue(subscriptionsResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testD_ConsumerSubscriptionListNotEmpty() throws Exception {
		logger.info("Running testD_ConsumerSubscriptionListNotEmpty");
		SubscribeTopicOperationResponse subscriptionListResponse = mockConsumerSubscriptionListOperation();

		Assert.notEmpty(subscriptionListResponse.getSubscriptions(),
				"Consumer subscription list is empty!");

		Assert.isTrue(subscriptionListResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Subscription list fetch operation failed!");

		Assert.isTrue(subscriptionListResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testE_ConsumerFetchEmptyData() throws Exception {
		logger.info("Running testE_ConsumerFetchEmptyData");
		FetchDataOperationResponse fetchDataOperationResponse = mockConsumerFetchDataOperation();

		Assert.isTrue(fetchDataOperationResponse.getFetchedContents().isEmpty(),
				"Fetched data is not empty");

		Assert.isTrue(fetchDataOperationResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Fetch consumer data operation failed!");

		Assert.isTrue(fetchDataOperationResponse.getResponseType() == ResponseType.INCOMING_DATA,
				"Response type is not of type communication!");
	}

	@Test
	public void testF_ConsumerUnsubscribeFromTopics() throws Exception {
		logger.info("Running testF_ConsumerUnsubscribeFromTopics");
		OperationResponse operationResponse = mockConsumerUnsubscribeOperation();

		Assert.isTrue(operationResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Unsubscribe operation failed!");

		Assert.isTrue(operationResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testG_ConsumerCommitSync() throws Exception {
		logger.info("Running testG_ConsumerCommitSync");
		OperationResponse operationResponse = mockConsumerCommitSyncOperation();

		Assert.isTrue(operationResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Commit sync operation failed!");

		Assert.isTrue(operationResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
	}

	@Test
	public void testH_ConsumerStop() throws Exception {
		logger.info("Running testH_ConsumerStop");
		OperationResponse operationResponse = mockConsumerStopOperation();

		Assert.isTrue(operationResponse.getOperationStatus() == OperationStatus.SUCCESS,
				"Stop operation failed!");

		Assert.isTrue(operationResponse.getResponseType() == ResponseType.COMMUNICATION,
				"Response type is not of type communication!");
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

	private CreatedConsumerOperationResponse mockCreateConsumerCreationOperation() throws Exception {
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

		return readMvcResultAs(mvcResult, CreatedConsumerOperationResponse.class);
	}

	private SubscribeTopicOperationResponse mockConsumerTopicSubscribeOperation() throws Exception {
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

		return readMvcResultAs(mvcResult, SubscribeTopicOperationResponse.class);
	}

	private FetchDataOperationResponse mockConsumerFetchDataOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/fetch", testConsumerId)
				.param("pollDuration", String.valueOf(1));

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, FetchDataOperationResponse.class);
	}

	private SubscribeTopicOperationResponse mockConsumerSubscriptionListOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/subscriptions", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, SubscribeTopicOperationResponse.class);
	}

	private OperationResponse mockConsumerUnsubscribeOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/unsubscribe", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, OperationResponse.class);
	}

	private OperationResponse mockConsumerStopOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/stop", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, OperationResponse.class);
	}

	private OperationResponse mockConsumerCommitSyncOperation() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(basePath + "/{consumerId}/commitSync", testConsumerId);

		MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		return readMvcResultAs(mvcResult, OperationResponse.class);
	}

	private <T> T readMvcResultAs(MvcResult mvcResult, Class<T> clazz)
			throws UnsupportedEncodingException, JsonProcessingException, JSONException {

		String responseContent = mvcResult.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseContent);
		logger.info("Response is: \n{}", json.toString(4));
		return objectMapper.readValue(responseContent, clazz);
	}
}
