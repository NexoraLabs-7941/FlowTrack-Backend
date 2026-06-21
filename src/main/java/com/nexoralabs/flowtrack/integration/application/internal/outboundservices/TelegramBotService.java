package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.TelegramMessage;

/**
 * Service interface for Telegram Bot API integration.
 */
public interface TelegramBotService {
    /**
     * Sends a message alert using the Telegram Bot API.
     * @param message The TelegramMessage containing the chatId and message content
     */
    void sendAlert(TelegramMessage message);
}
