package com.nexoralabs.flowtrack.integration.interfaces.rest;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.FirebaseService;
import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.GoogleSheetsService;
import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.TelegramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller to test integration configurations.
 */
@RestController
@RequestMapping(value = "api/v1/integrations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Integrations", description = "External API Integration management API")
public class IntegrationController {

    private final GoogleSheetsService googleSheetsService;
    private final TelegramService telegramService;
    private final FirebaseService firebaseService;

    public IntegrationController(GoogleSheetsService googleSheetsService,
                                 TelegramService telegramService,
                                 FirebaseService firebaseService) {
        this.googleSheetsService = googleSheetsService;
        this.telegramService = telegramService;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/google-sheets/test")
    @Operation(summary = "Test Google Sheets export stub", description = "Triggers a test call to the Google Sheets integration service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration triggered successfully")
    })
    public ResponseEntity<String> testGoogleSheets() {
        List<String> headers = List.of("ID", "Product Name", "Stock");
        List<Map<String, Object>> rows = List.of(
                Map.of("ID", "1", "Product Name", "Test Product A", "Stock", 50),
                Map.of("ID", "2", "Product Name", "Test Product B", "Stock", 120)
        );
        googleSheetsService.exportReport("Test Inventory Report", headers, rows);
        return ResponseEntity.ok("Google Sheets test integration triggered successfully (check server logs).");
    }

    @PostMapping("/telegram/test")
    @Operation(summary = "Test Telegram alert stub", description = "Triggers a test alert message to the Telegram integration service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration triggered successfully")
    })
    public ResponseEntity<String> testTelegram(@RequestParam(defaultValue = "Critical alert test message from Flowtrack") String message) {
        telegramService.sendCriticalAlert(message);
        return ResponseEntity.ok("Telegram test integration triggered successfully (check server logs).");
    }

    @PostMapping("/firebase/test")
    @Operation(summary = "Test Firebase notification stub", description = "Triggers a test push notification to the Firebase integration service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Integration triggered successfully")
    })
    public ResponseEntity<String> testFirebase(@RequestParam(defaultValue = "all-devices") String target,
                                               @RequestParam(defaultValue = "Test Alert") String title,
                                               @RequestParam(defaultValue = "This is a test push notification from Flowtrack.") String body) {
        firebaseService.sendPushNotification(target, title, body);
        return ResponseEntity.ok("Firebase test integration triggered successfully (check server logs).");
    }
}
