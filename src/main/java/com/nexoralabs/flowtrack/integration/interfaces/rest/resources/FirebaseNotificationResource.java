package com.nexoralabs.flowtrack.integration.interfaces.rest.resources;

import java.util.Map;

/**
 * Resource representing a request to dispatch a Firebase push notification.
 */
public record FirebaseNotificationResource(
        String token,
        String title,
        String body,
        Map<String, String> data
) {
    public FirebaseNotificationResource {
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
