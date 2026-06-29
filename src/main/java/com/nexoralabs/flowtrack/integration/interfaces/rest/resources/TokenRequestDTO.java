package com.nexoralabs.flowtrack.integration.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a request payload to register an FCM device token.
 */
public record TokenRequestDTO(
    @NotBlank(message = "User ID must not be blank")
    String userId,
    
    @NotBlank(message = "Firebase Token must not be blank")
    String token,
    
    String deviceType
) {}
