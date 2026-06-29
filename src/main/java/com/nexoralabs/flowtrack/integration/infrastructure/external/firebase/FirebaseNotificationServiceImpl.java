package com.nexoralabs.flowtrack.integration.infrastructure.external.firebase;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.FirebaseNotificationService;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.NotificationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the FirebaseNotificationService interface.
 */
@Service
public class FirebaseNotificationServiceImpl implements FirebaseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationServiceImpl.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;

    public FirebaseNotificationServiceImpl(RestTemplate restTemplate,
                                            @Value("${integration.firebase.api-url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Override
    public void sendPushNotification(NotificationDetails notification) {
        log.info("Sending FCM Push Notification (API URL: {})", apiUrl);
        log.info("To device/topic token: {}", notification.token());
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("to", notification.token());
            
            Map<String, String> notificationMap = new HashMap<>();
            notificationMap.put("title", notification.title());
            notificationMap.put("body", notification.body());
            payload.put("notification", notificationMap);
            
            if (notification.data() != null && !notification.data().isEmpty()) {
                payload.put("data", notification.data());
            }
            
            log.debug("FCM API request payload: {}", payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "key=mock_firebase_server_key");
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
            
            if (apiUrl.contains("googleapis.com")) {
                log.info("[MOCK] Simulating Firebase Cloud Messaging dispatch. Payload: {}", payload);
            } else {
                restTemplate.postForLocation(apiUrl, requestEntity);
                log.info("FCM Push Notification successfully dispatched.");
            }
        } catch (Exception e) {
            log.error("Failed to send Firebase push notification", e);
            throw new RuntimeException("Firebase integration error: " + e.getMessage());
        }
    }
}
