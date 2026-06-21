package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.NotificationDetails;

/**
 * Service interface for Firebase Cloud Messaging (FCM) integration.
 */
public interface FirebaseNotificationService {
    /**
     * Sends a push notification using Firebase Cloud Messaging.
     * @param notification The NotificationDetails containing target token, title, body, and payload data
     */
    void sendPushNotification(NotificationDetails notification);
}
