package com.wafka.service.impl;

import com.wafka.service.IAutoConsumerOperationService;
import com.wafka.service.IConsumerService;
import com.wafka.service.IConsumerWebSocketSessionService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.CloseReason;
import java.io.IOException;

@Service
public class DeadConsumerThreadDetectorService {
	private final Logger logger;

	private final IConsumerService iConsumerService;

	private final IAutoConsumerOperationService iAutoConsumerOperationService;

	private final IConsumerWebSocketSessionService iConsumerWebSocketSessionService;

	@Autowired
	public DeadConsumerThreadDetectorService(Logger logger, IConsumerService iConsumerService,
											 IAutoConsumerOperationService iAutoConsumerOperationService,
											 IConsumerWebSocketSessionService iConsumerWebSocketSessionService) {

		this.logger = logger;
		this.iConsumerService = iConsumerService;
		this.iAutoConsumerOperationService = iAutoConsumerOperationService;
		this.iConsumerWebSocketSessionService = iConsumerWebSocketSessionService;
	}

	@PostConstruct
	private void init() {
		logger.info("Succesfully initialized the dead consumer thread detector service.");
	}

	@Scheduled(fixedDelay = 2000)
	public void performOperation() {
		iConsumerService.getRegisteredConsumers().forEach(iConsumerId -> {
			if (!iAutoConsumerOperationService.isRunning(iConsumerId)) {
				iAutoConsumerOperationService.stop(iConsumerId);
				logger.warn("Detected and removed dead consumer thread {} from the map.", iConsumerId);

				CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION,
						"Consumer thread not running anymore");

				try {
					iConsumerWebSocketSessionService.close(iConsumerId, closeReason);
					logger.warn("Session closed for consumer {}", iConsumerId);
				} catch (IOException exception) {
					logger.error("Cloud not close session for consumer {} due to {}", iConsumerId, exception.getMessage());
				} finally {
					iConsumerWebSocketSessionService.delete(iConsumerId);
				}
			}
		});
	}
}
