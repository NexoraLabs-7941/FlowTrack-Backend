package com.nexoralabs.flowtrack.integration.application.internal;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nexoralabs.flowtrack.integration.domain.model.entities.StoredNotification;
import com.nexoralabs.flowtrack.integration.domain.model.entities.UserDevice;
import com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories.StoredNotificationRepository;
import com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service orchestrating push notification dispatches using the Firebase Admin SDK.
 * Supports asynchronous delivery and runs in Mock Mode if Firebase App is not initialized.
 */
@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final UserDeviceRepository userDeviceRepository;
    private final StoredNotificationRepository storedNotificationRepository;

    public PushNotificationService(UserDeviceRepository userDeviceRepository,
                                   StoredNotificationRepository storedNotificationRepository) {
        this.userDeviceRepository = userDeviceRepository;
        this.storedNotificationRepository = storedNotificationRepository;
    }

    /**
     * Sends a push notification to all active devices registered by a user.
     * @param userId Target user ID
     * @param title Title of the push notification
     * @param body Body of the push notification
     */
    public void sendPushToUser(String userId, String title, String body) {
        sendPushToUser(userId, title, body, "info");
    }

    /**
     * Sends a push notification to all active devices registered by a user, specifying a custom type.
     * @param userId Target user ID
     * @param title Title of the push notification
     * @param body Body of the push notification
     * @param type Notification type (e.g. info, alert, warning)
     */
    public void sendPushToUser(String userId, String title, String body, String type) {
        log.info("Initiating push notification dispatch for user: {} with type: {}", userId, type);

        // Persist the notification in the database for dashboard display
        try {
            StoredNotification stored = new StoredNotification(userId, title, body, type);
            storedNotificationRepository.save(stored);
            log.info("Persisted notification in DB for user: {} with type: {}", userId, type);
        } catch (Exception e) {
            log.error("Failed to persist notification in DB for user: {}", userId, e);
        }

        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);

        if (devices.isEmpty()) {
            log.warn("No active device tokens found for user: {}", userId);
            return;
        }

        // Check if Firebase App has been initialized
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("[MOCK MODE] Firebase App not initialized. Simulating push dispatch to {} tokens for user {}", 
                     devices.size(), userId);
            devices.forEach(device -> log.info("[MOCK] Sent push notification '{}' - '{}' to token: {}", 
                                                title, body, device.getFirebaseToken()));
            return;
        }

        for (UserDevice device : devices) {
            try {
                Notification notificationPayload = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                Message message = Message.builder()
                        .setToken(device.getFirebaseToken())
                        .setNotification(notificationPayload)
                        .build();

                // Send asynchronously to prevent thread blocking
                FirebaseMessaging.getInstance().sendAsync(message);
                log.info("Dispatched message async payload to token: {}", device.getFirebaseToken());
            } catch (Exception e) {
                log.error("Failed to send push notification to token: {}", device.getFirebaseToken(), e);
            }
        }
    }

    /**
     * Dispatches a push notification to all unique users who have registered devices, plus the test-user.
     * @param title Notification title
     * @param body Notification body
     * @param type Notification type
     */
    public void sendPushToAllUsers(String title, String body, String type) {
        log.info("Initiating broadcast push notification for all users with type: {}", type);
        try {
            List<String> userIds = userDeviceRepository.findAll().stream()
                    .map(UserDevice::getUserId)
                    .filter(uid -> uid != null && !uid.isBlank())
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            if (!userIds.contains("test-user")) {
                userIds.add("test-user");
            }

            log.info("Broadcasting to users: {}", userIds);
            for (String userId : userIds) {
                sendPushToUser(userId, title, body, type);
            }
        } catch (Exception e) {
            log.error("Failed to broadcast push notification to all users", e);
        }
    }

}
