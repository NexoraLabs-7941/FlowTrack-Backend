package com.nexoralabs.flowtrack.integration.infrastructure.services;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.FirebaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stub implementation of the FirebaseService.
 */
@Service
public class FirebaseServiceImpl implements FirebaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseServiceImpl.class);

    @Override
    public void sendPushNotification(String target, String title, String body) {
        LOGGER.info("Stub: Sending Firebase push notification to target: '{}' with title: '{}' and body: '{}'",
                target, title, body);
        // TODO: Implement actual connection to Firebase Cloud Messaging API
    }
}
