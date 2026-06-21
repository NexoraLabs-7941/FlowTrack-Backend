package com.nexoralabs.flowtrack.integration.interfaces.rest.resources;

/**
 * Resource representing a request to dispatch a Telegram alert message.
 */
public record TelegramAlertResource(
        String chatId,
        String text
) {
    public TelegramAlertResource {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("Chat ID must not be null or blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Message text must not be null or blank");
        }
    }
}
