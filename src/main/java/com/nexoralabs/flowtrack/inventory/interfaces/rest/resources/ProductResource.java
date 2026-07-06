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
    String barcode,
    Integer minStock,
    Double unitPrice,
    Boolean isActive
) {}
