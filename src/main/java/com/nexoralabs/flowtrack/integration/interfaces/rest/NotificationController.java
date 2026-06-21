package com.nexoralabs.flowtrack.integration.interfaces.rest;

import com.nexoralabs.flowtrack.integration.application.internal.PushNotificationService;
import com.nexoralabs.flowtrack.integration.domain.model.entities.UserDevice;
import com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories.StoredNotificationRepository;
import com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories.UserDeviceRepository;
import com.nexoralabs.flowtrack.integration.interfaces.rest.resources.TokenRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST Controller handling push notification token registrations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Endpoints for registering client tokens and push notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final UserDeviceRepository userDeviceRepository;
    private final PushNotificationService pushNotificationService;
    private final StoredNotificationRepository storedNotificationRepository;

    public NotificationController(UserDeviceRepository userDeviceRepository,
                                  PushNotificationService pushNotificationService,
                                  StoredNotificationRepository storedNotificationRepository) {
        this.userDeviceRepository = userDeviceRepository;
        this.pushNotificationService = pushNotificationService;
        this.storedNotificationRepository = storedNotificationRepository;
    }

    /**
     * Registers or updates a client FCM registration token to persist user devices.
     * @param request TokenRequestDTO registration payload
     * @return Confirmation message
     */
    @PostMapping("/register-token")
    @Operation(summary = "Register FCM Device Token", description = "Associates an FCM device token with a user ID to enable push notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device token registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<Map<String, String>> registerToken(@Valid @RequestBody TokenRequestDTO request) {
        log.info("Received request to register token for user: {} (Device: {})", 
                 request.userId(), request.deviceType());

        Optional<UserDevice> existing = userDeviceRepository.findByFirebaseToken(request.token());
        
        if (existing.isPresent()) {
            UserDevice device = existing.get();
            device.setUserId(request.userId());
            device.setDeviceType(request.deviceType() != null ? request.deviceType() : "web");
            userDeviceRepository.save(device);
            log.info("FCM Token already registered. Updated associated user details.");
        } else {
            UserDevice device = new UserDevice(request.userId(), request.token(), request.deviceType());
            userDeviceRepository.save(device);
            log.info("New FCM Token successfully registered.");
        }

        return ResponseEntity.ok(Map.of("message", "Device token registered successfully"));
    }

    /**
     * Sends a test push notification to a specified user.
     * @param userId Target user ID
     * @param title Title of the push notification
     * @param body Body of the push notification
     * @return Confirmation message
     */
    @PostMapping("/send-test")
    @Operation(summary = "Send Test Notification to User", description = "Dispatches a push notification to all active devices registered under the userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test notification queued successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<Map<String, String>> sendTestNotification(@RequestParam String userId,
                                                                   @RequestParam String title,
                                                                   @RequestParam String body) {
        log.info("Test push notification requested for user: '{}' with title: '{}'", userId, title);
        pushNotificationService.sendPushToUser(userId, title, body);
        return ResponseEntity.ok(Map.of("message", "Test push notification dispatched successfully"));
    }

    /**
     * Deletes a stored notification by its database ID.
     * @param id Notification database ID
     * @return Confirmation message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Stored Notification", description = "Deletes a persisted notification from the database by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid token required")
    })
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        log.info("Request to delete notification with ID: {}", id);
        if (storedNotificationRepository.existsById(id)) {
            storedNotificationRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}

