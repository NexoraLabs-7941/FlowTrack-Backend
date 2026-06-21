package com.nexoralabs.flowtrack.inventory.domain.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String productName) {
        super("Product with name '%s' already exists".formatted(productName));
    }
}
