package com.wafka.controller;

import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.model.response.*;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IConsumerService;
import com.wafka.service.IManualConsumerOperationService;
import com.wafka.types.ConsumerParameter;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Component
@RequestMapping("/kafka/consumer/rest/v1")
@Api(tags = "kafka-consumer-rest-controller", value = "Kafka Consumer REST controller")
public class KafkaConsumerRestController {
	private static final String MESSAGE_FIELD = "message";

	@Autowired
	private Logger logger;

	@Autowired
	private IManualConsumerOperationService iManualConsumerOperationService;

	@Autowired
	private IConsumerService iConsumerService;

	@Autowired
	private IConsumerPropertyFactory iConsumerPropertyFactory;

	@Autowired
	@ConsumerIdProtocol(Protocol.REST)
	private IConsumerIdFactory iConsumerIdFactory;

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> home() {
		Map<String, Object> response = new HashMap<>();
		response.put("controller", getClass().getName());
		response.put(MESSAGE_FIELD, "Controller is normally available");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IResponse> listConsumers() {
		Set<ConsumerId> consumersIds = iConsumerService.getRegisteredConsumers();

		RegisteredConsumersResponse registeredConsumersResponse = new RegisteredConsumersResponse();
		registeredConsumersResponse.setConsumers(consumersIds);
		registeredConsumersResponse.setResponseType(ResponseType.COMMUNICATION);

		return new ResponseEntity<>(registeredConsumersResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/{consumerId}/{groupId}/create", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OperationResponse> createConsumer(
			@PathVariable("consumerId") String consumerId,
			@PathVariable("groupId") String groupId,
			@RequestParam("enableAutoCommit") Boolean enableAutoCommit,
			@RequestParam("kafkaClusterId") String kafkaClusterId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received create consumer request with id {}.", iConsumerId);

		Map<ConsumerParameter, Object> consumerParameters = new EnumMap<>(ConsumerParameter.class);
		consumerParameters.put(ConsumerParameter.KAFKA_CLUSTER_URI, kafkaClusterId);
		consumerParameters.put(ConsumerParameter.GROUP_ID, groupId);
		consumerParameters.put(ConsumerParameter.ENABLE_AUTO_COMMIT, enableAutoCommit);
		consumerParameters.put(ConsumerParameter.CONSUMER_ID, consumerId);

		Properties consumerProperties = iConsumerPropertyFactory.getProperties(consumerParameters);
		iConsumerService.create(iConsumerId, consumerProperties);

		CreatedConsumerOperationResponse createdConsumerOperationResponse = new CreatedConsumerOperationResponse();
		createdConsumerOperationResponse.setConsumerParameters(consumerParameters);
		createdConsumerOperationResponse.setConsumerId(iConsumerId);
		createdConsumerOperationResponse.setResponseType(ResponseType.COMMUNICATION);
		createdConsumerOperationResponse.setOperationStatus(OperationStatus.SUCCESS);

		return new ResponseEntity<>(createdConsumerOperationResponse, HttpStatus.CREATED);
	}

	@PostMapping(value = "/{consumerId}/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IOperationResponse> subscribeTopics(
			@PathVariable("consumerId") String consumerId,
			@RequestBody List<String> topics) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received subscription request from for consumer {}.", iConsumerId);

		Set<String> topicSet = new HashSet<>(topics);
		OperationStatus operationStatus = iManualConsumerOperationService.subscribe(iConsumerId, topicSet);

		SubscribeTopicOperationResponse subscribeTopicOperationResponse = new SubscribeTopicOperationResponse();
		subscribeTopicOperationResponse.setConsumerId(iConsumerId);
		subscribeTopicOperationResponse.setSubscriptions(topicSet);
		subscribeTopicOperationResponse.setOperationStatus(operationStatus);
		subscribeTopicOperationResponse.setResponseType(ResponseType.COMMUNICATION);

		return new ResponseEntity<>(subscribeTopicOperationResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IOperationResponse> fetchData(
			@PathVariable("consumerId") String consumerId,
			@RequestParam("pollDuration") Integer pollDurationSeconds) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received fetch request for consumer {}.", iConsumerId);

		List<FetchedContent> fetchedContents = iManualConsumerOperationService.fetch(
				iConsumerId, pollDurationSeconds);

		FetchDataOperationResponse fetchDataOperationResponse = new FetchDataOperationResponse(fetchedContents);
		fetchDataOperationResponse.setResponseType(ResponseType.INCOMING_DATA);
		fetchDataOperationResponse.setConsumerId(iConsumerId);
		fetchDataOperationResponse.setOperationStatus(OperationStatus.SUCCESS);

		return new ResponseEntity<>(fetchDataOperationResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/commitSync", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IOperationResponse> commitSync(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received commit sync request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.commitSync(iConsumerId);

		OperationResponse operationResponse = new OperationResponse();
		operationResponse.setConsumerId(iConsumerId);
		operationResponse.setResponseType(ResponseType.COMMUNICATION);
		operationResponse.setOperationStatus(operationStatus);

		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IOperationResponse> stop(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received close request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.stop(iConsumerId);

		OperationResponse operationResponse = new OperationResponse();
		operationResponse.setConsumerId(iConsumerId);
		operationResponse.setResponseType(ResponseType.COMMUNICATION);
		operationResponse.setOperationStatus(operationStatus);

		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IOperationResponse> unsubscribe(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received unsuscribe request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.unsubscribe(iConsumerId);

		OperationResponse operationResponse = new OperationResponse();
		operationResponse.setConsumerId(iConsumerId);
		operationResponse.setResponseType(ResponseType.COMMUNICATION);
		operationResponse.setOperationStatus(operationStatus);

		return new ResponseEntity<>(operationResponse, HttpStatus.OK);
	}

    @GetMapping(value = "/{consumerId}/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IOperationResponse> subscriptions(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received subscriptions list request for consumer {}.", iConsumerId);

		Set<String> subscriptions = iManualConsumerOperationService.getSubscriptions(iConsumerId);

		SubscribeTopicOperationResponse subscribeTopicOperationResponse = new SubscribeTopicOperationResponse();
		subscribeTopicOperationResponse.setConsumerId(iConsumerId);
		subscribeTopicOperationResponse.setSubscriptions(subscriptions);
		subscribeTopicOperationResponse.setResponseType(ResponseType.COMMUNICATION);
		subscribeTopicOperationResponse.setOperationStatus(OperationStatus.SUCCESS);

		return new ResponseEntity<>(subscribeTopicOperationResponse, HttpStatus.OK);
	}
}
