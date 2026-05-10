package com.nexoralabs.flowtrack.inventory.interfaces.rest.resources;

/**
 * ProductResource
 */
public record ProductResource (
    Long id,
    String name,
    String description,
    String categoryId,
    String providerId,
    Integer minStock,
    Double unitPrice,
    Boolean isActive
) {}
