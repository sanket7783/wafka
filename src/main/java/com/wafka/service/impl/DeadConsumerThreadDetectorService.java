package com.wafka.service.impl;

import com.wafka.exception.NoSuchConsumerThreadException;
import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerService;
import com.wafka.service.IConsumerWebSocketSessionService;
import com.wafka.types.OperationStatus;
import com.wafka.types.Protocol;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.CloseReason;
import java.io.IOException;

@Service
public class DeadConsumerThreadDetectorService {
	@Autowired
	private Logger logger;

	@Autowired
	private IConsumerService iConsumerService;

	@Autowired
	private IAutoConsumerOperationService iAutoConsumerOperationService;

	@Autowired
	private IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Scheduled(fixedDelay = 120000)
	public void performOperationEveryTwoMinutes() {
		iConsumerService.getRegisteredConsumers().forEach(iConsumerId -> {
			if (!iAutoConsumerOperationService.isRunning(iConsumerId) &&
					iConsumerId.getProtocolType() == Protocol.WEBSOCKET) {

				try {
					OperationStatus stopOperationStatus = iAutoConsumerOperationService.stop(iConsumerId);
					if (stopOperationStatus == OperationStatus.SUCCESS) {
						logger.info("Detected a dead consumer thread {}.", iConsumerId);
					} else {
						logger.warn("Could not stop and remove the consumer thread {}", iConsumerId);
					}
				} catch (NoSuchConsumerThreadException exception) {
					logger.warn("Could not stop the consumer thread {} because it doesn't exists.", iConsumerId);
				}

				CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION,
						"Consumer thread " + iConsumerId + " not running anymore");

				try {
					iConsumerWebSocketSessionService.close(iConsumerId, closeReason);
					logger.info("Session closed for consumer {}", iConsumerId);

				} catch (IOException exception) {
					logger.error("Error while closing session for consumer {} due to {}",
							iConsumerId, exception.getMessage());

				} finally {
					iConsumerWebSocketSessionService.delete(iConsumerId);
				}
			}
		});
	}
}
