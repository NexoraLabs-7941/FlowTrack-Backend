package com.nexoralabs.flowtrack.inventory.interfaces.rest.resources;

/**
 * Provider resource
 */
public record ProviderResource(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String ruc
) {}
