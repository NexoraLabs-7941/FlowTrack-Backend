package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

/**
 * Service contract for Telegram integration.
 */
public interface TelegramService {
    /**
     * Sends a markdown formatted message alert to a configured Telegram channel or chat.
     *
     * @param message Text message content
     */
    void sendCriticalAlert(String message);
}
