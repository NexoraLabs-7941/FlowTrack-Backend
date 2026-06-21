package com.nexoralabs.flowtrack.integration.domain.model.valueobjects;

/**
 * Value object representing message details for Telegram Bot.
 */
public record TelegramMessage(String chatId, String text) {
    public TelegramMessage {
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("Chat ID must not be null or blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Message text must not be null or blank");
        }
    }
}
