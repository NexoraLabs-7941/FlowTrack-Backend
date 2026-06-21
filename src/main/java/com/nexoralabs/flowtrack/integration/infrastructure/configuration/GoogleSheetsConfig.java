package com.nexoralabs.flowtrack.integration.infrastructure.configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class to initialize the Google Sheets and Drive API clients.
 * Uses the same service account credentials configured for Firebase.
 */
@Configuration
public class GoogleSheetsConfig {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsConfig.class);

    private final ResourceLoader resourceLoader;

    @Value("${app.firebase.config-path}")
    private String configPath;

    public GoogleSheetsConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private GoogleCredentials getCredentials() throws Exception {
        Resource resource = resourceLoader.getResource(configPath);
        if (!resource.exists()) {
            throw new IllegalStateException("Google service account credentials file not found at " + configPath);
        }
        try (InputStream in = resource.getInputStream()) {
            List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/spreadsheets",
                "https://www.googleapis.com/auth/drive"
            );
            return GoogleCredentials.fromStream(in).createScoped(scopes);
        }
    }

    @Bean
    public Sheets googleSheets() {
        try {
            log.info("Initializing Google Sheets service bean using credentials path: {}", configPath);
            GoogleCredentials credentials = getCredentials();
            return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Flowtrack")
                .build();
        } catch (Exception e) {
            log.error("Failed to initialize Google Sheets service. Sheets integration will not function.", e);
            return null;
        }
    }

    @Bean
    public Drive googleDrive() {
        try {
            log.info("Initializing Google Drive service bean using credentials path: {}", configPath);
            GoogleCredentials credentials = getCredentials();
            return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Flowtrack")
                .build();
        } catch (Exception e) {
            log.error("Failed to initialize Google Drive service. Drive integration will not function.", e);
            return null;
        }
    }
}
