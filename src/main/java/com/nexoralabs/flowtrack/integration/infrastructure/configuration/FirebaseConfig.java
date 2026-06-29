package com.nexoralabs.flowtrack.integration.infrastructure.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

/**
 * Configuration class to initialize the Firebase Admin SDK.
 * Safely handles missing credential files to avoid context crashes in development.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final ResourceLoader resourceLoader;

    @Value("${app.firebase.config-path}")
    private String configPath;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            log.info("Loading Firebase Service Account from path: {}", configPath);
            Resource resource = resourceLoader.getResource(configPath);
            
            if (!resource.exists()) {
                log.warn("Firebase credential file does not exist at [{}]. Firebase Admin functionality will run in MOCK mode.", configPath);
                return;
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase Admin SDK successfully initialized.");
                } else {
                    log.info("Firebase Admin App is already initialized.");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while initializing Firebase Admin SDK", e);
        }
    }
}
