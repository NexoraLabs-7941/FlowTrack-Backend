package com.nexoralabs.flowtrack.integration.interfaces.rest;

import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.NotificationDetails;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.SheetReport;
import com.nexoralabs.flowtrack.integration.interfaces.acl.IntegrationContextFacade;
import com.nexoralabs.flowtrack.integration.interfaces.rest.resources.FirebaseNotificationResource;
import com.nexoralabs.flowtrack.integration.interfaces.rest.resources.GoogleSheetsReportResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller for external service integration orchestration.
 * Centralizes endpoint pathways to test or invoke Google Sheets and Firebase.
 */
@RestController
@RequestMapping(value = "/api/v1/integration", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Integration", description = "Endpoints for orchestrating and testing external service integrations (Google Sheets, Firebase)")
@SecurityRequirement(name = "bearerAuth")
public class IntegrationController {

    private final IntegrationContextFacade integrationContextFacade;

    public IntegrationController(IntegrationContextFacade integrationContextFacade) {
        this.integrationContextFacade = integrationContextFacade;
    }

    /**
     * Exports generated report data to Google Sheets.
     * @param resource The GoogleSheetsReportResource request payload
     * @return Confirmation message and sheet URL
     */
    @PostMapping("/google-sheets/export")
    @Operation(summary = "Export data to Google Sheets", description = "Generates and exports data to Google Sheets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report exported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<Map<String, String>> exportToGoogleSheets(@RequestBody GoogleSheetsReportResource resource) {
        SheetReport report = new SheetReport(resource.spreadsheetName(), resource.rows());
        String sheetUrl = integrationContextFacade.exportReport(report);
        return ResponseEntity.ok(Map.of("message", "Report exported successfully", "url", sheetUrl));
    }

    /**
     * Sends a push notification using Firebase Cloud Messaging.
     * @param resource The FirebaseNotificationResource request payload
     * @return Confirmation message
     */
    @PostMapping("/firebase/notification")
    @Operation(summary = "Send Firebase Push Notification", description = "Sends a push notification via Firebase Cloud Messaging API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification dispatched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<Map<String, String>> sendPushNotification(@RequestBody FirebaseNotificationResource resource) {
        NotificationDetails notification = new NotificationDetails(
                resource.token(),
                resource.title(),
                resource.body(),
                resource.data()
        );
        integrationContextFacade.sendPushNotification(notification);
        return ResponseEntity.ok(Map.of("message", "Firebase push notification dispatched successfully"));
    }
}
