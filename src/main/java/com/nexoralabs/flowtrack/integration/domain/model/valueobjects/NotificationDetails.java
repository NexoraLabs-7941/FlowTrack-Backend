package com.nexoralabs.flowtrack.integration.domain.model.valueobjects;

import java.util.Map;

/**
 * Value object representing push notification details for Firebase Cloud.
 */
public record NotificationDetails(String token, String title, String body, Map<String, String> data) {
    public NotificationDetails {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be null or blank");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Body must not be null or blank");
        }
    }
}
