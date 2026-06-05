package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

/**
 * Service contract for Firebase Cloud Messaging integration.
 */
public interface FirebaseService {
    /**
     * Sends a push notification to a specific target device token or topic.
     *
     * @param target Token or Topic identifier
     * @param title  Title of notification
     * @param body   Body content
     */
    void sendPushNotification(String target, String title, String body);
}
