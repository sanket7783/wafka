package com.wafka.controller;

import com.wafka.model.ExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class KafkaRestControllerAdvice extends ResponseEntityExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRestControllerAdvice.class);

	@ResponseBody
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleGenericError(
			Exception exception, WebRequest request) {

		return createError(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception, HttpHeaders headers,
			HttpStatus httpStatus, WebRequest request) {

		return createError(exception, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<Object> createError(Exception exception, HttpStatus httpStatus) {
		LOGGER.error(exception.getMessage(), exception);
		return createResponse(exception.getMessage(), httpStatus);
	}

	private ResponseEntity<Object> createResponse(String message, HttpStatus httpStatus) {
		ExceptionMessage exceptionMessage = new ExceptionMessage(
				message, httpStatus.value(), httpStatus.getReasonPhrase()
		);
		return ResponseEntity.status(httpStatus).body(exceptionMessage);
	}
}
