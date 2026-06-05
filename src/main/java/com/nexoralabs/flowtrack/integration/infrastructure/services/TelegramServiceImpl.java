package com.nexoralabs.flowtrack.integration.infrastructure.services;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stub implementation of the TelegramService.
 */
@Service
public class TelegramServiceImpl implements TelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Override
    public void sendCriticalAlert(String message) {
        LOGGER.info("Stub: Sending critical alert to Telegram with message: '{}'", message);
        // TODO: Implement actual connection to Telegram Bot API
    }
}
