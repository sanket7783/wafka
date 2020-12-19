package com.wafka.controller;

import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.ConsumerId;
import com.wafka.model.FetchedContent;
import com.wafka.model.response.IConsumerResponse;
import com.wafka.model.response.IResponse;
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

	@Autowired
	private IResponseFactory iResponseFactory;

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
		IResponse response = iResponseFactory.getResponse(consumersIds);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/{consumerId}/{groupId}/create", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createConsumer(
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

		// Fill the response with the supplied parameters.
		Map<String, Object> response = new HashMap<>();
		consumerParameters.forEach((consumerParameter, parameterValue) ->
				response.put(consumerParameter.getDescription(), parameterValue));

		response.put(MESSAGE_FIELD, "Consumer successfully created.");

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping(value = "/{consumerId}/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IConsumerResponse> subscribeTopics(
			@PathVariable("consumerId") String consumerId,
			@RequestBody List<String> topics) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received subscription request from for consumer {}.", iConsumerId);

		Set<String> topicSet = new HashSet<>(topics);
		OperationStatus operationStatus = iManualConsumerOperationService.subscribe(iConsumerId, topicSet);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId, topicSet, operationStatus);
		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IConsumerResponse> fetchData(
			@PathVariable("consumerId") String consumerId,
			@RequestParam("pollDuration") Integer pollDurationSeconds) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received fetch request for consumer {}.", iConsumerId);

		List<FetchedContent> fetchedContents = iManualConsumerOperationService.fetch(
				iConsumerId, pollDurationSeconds);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId, fetchedContents,
				OperationStatus.SUCCESS);

		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/commitSync", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IConsumerResponse> commitSync(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received commit sync request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.commitSync(iConsumerId);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId,
				ResponseType.COMMUNICATION, operationStatus);

		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IConsumerResponse> stop(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received close request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.stop(iConsumerId);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId,
				ResponseType.COMMUNICATION, operationStatus);

		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IConsumerResponse> unsubscribe(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received unsuscribe request for consumer {}.", iConsumerId);

		OperationStatus operationStatus = iManualConsumerOperationService.unsubscribe(iConsumerId);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId,
				ResponseType.COMMUNICATION, operationStatus);

		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}

    @GetMapping(value = "/{consumerId}/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IConsumerResponse> subscriptions(
			@PathVariable("consumerId") String consumerId) {

		ConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received subscriptions list request for consumer {}.", iConsumerId);

		Set<String> subscriptions = iManualConsumerOperationService.getSubscriptions(iConsumerId);

		IConsumerResponse iConsumerResponse = iResponseFactory.getResponse(iConsumerId, subscriptions,
				OperationStatus.SUCCESS);

		return new ResponseEntity<>(iConsumerResponse, HttpStatus.OK);
	}
}
