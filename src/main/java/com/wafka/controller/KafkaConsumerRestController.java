package com.wafka.controller;

import com.wafka.factory.IConsumerIdFactory;
import com.wafka.factory.IConsumerPropertyFactory;
import com.wafka.factory.IResponseFactory;
import com.wafka.model.IConsumerId;
import com.wafka.model.IFetchedContent;
import com.wafka.model.IResponse;
import com.wafka.qualifiers.ConsumerIdProtocol;
import com.wafka.service.IConsumerService;
import com.wafka.service.IManualConsumerOperationService;
import com.wafka.types.ConsumerParameter;
import com.wafka.types.Protocol;
import com.wafka.types.ResponseType;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
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
	private static final String CONSUMER_ID_FIELD = "consumerId";
	private static final String MESSAGE_FIELD = "message";
	private static final String DATA_FIELD = "data";

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
	public ResponseEntity<Object> listConsumers() {
		Set<IConsumerId> consumersIds = iConsumerService.getRegisteredConsumers();

		Map<String, Object> response = new HashMap<>();
		response.put("consumers", consumersIds);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/{consumerId}/{groupId}/create", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createConsumer(
			@PathVariable("consumerId") String consumerId,
			@PathVariable("groupId") String groupId,
			@RequestParam("enableAutoCommit") @DefaultValue("true") Boolean enableAutoCommit) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received create consumer request with id {}.", iConsumerId);

		Map<ConsumerParameter, Object> consumerParameters = new EnumMap<>(ConsumerParameter.class);
		consumerParameters.put(ConsumerParameter.GROUP_ID, groupId);
		consumerParameters.put(ConsumerParameter.ENABLE_AUTO_COMMIT, enableAutoCommit);
		consumerParameters.put(ConsumerParameter.CONSUMER_ID, consumerId);

		Properties consumerProperties = iConsumerPropertyFactory.getProperties(consumerParameters);
		iConsumerService.create(iConsumerId, consumerProperties);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Consumer succesfully created.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/{consumerId}/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> subscribeTopics(
			@PathVariable("consumerId") String consumerId,
			List<String> topics) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received subscription request from for consumer {}.", iConsumerId);

		iManualConsumerOperationService.subscribe(iConsumerId, topics);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Subscriptions updated.");
		response.put("subscriptions", topics);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> fetchData(
			@PathVariable("consumerId") String consumerId,
			@RequestParam("pollDuration") @DefaultValue("1") Integer pollDurationSeconds) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received fetch request for consumer {}.", iConsumerId);

		List<IFetchedContent> fetchedContents = iManualConsumerOperationService.fetch(
				iConsumerId, pollDurationSeconds);

		IResponse iResponse = iResponseFactory.getResponse(
				ResponseType.INCOMING_DATA, "Succesfully fetched data.", fetchedContents);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Succesfully fetched data from topics.");
		response.put(DATA_FIELD, iResponse);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/commitSync", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> commitSync(
			@PathVariable("consumerId") String consumerId) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received commit sync request for consumer {}.", iConsumerId);

		iManualConsumerOperationService.commitSync(iConsumerId);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Committed succesfully in sync mode.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> stop(
			@PathVariable("consumerId") String consumerId) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received close request for consumer {}.", iConsumerId);

		iManualConsumerOperationService.stop(iConsumerId);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Succesfully stopped consumer.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/{consumerId}/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> unsubscribe(
			@PathVariable("consumerId") String consumerId) {

		IConsumerId iConsumerId = iConsumerIdFactory.getConsumerId(consumerId);
		logger.info("Received unsuscribe request for consumer {}.", iConsumerId);

		iManualConsumerOperationService.unsubscribe(iConsumerId);

		Map<String, Object> response = new HashMap<>();
		response.put(CONSUMER_ID_FIELD, consumerId);
		response.put(MESSAGE_FIELD, "Succesfully unsubscribed topics.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
