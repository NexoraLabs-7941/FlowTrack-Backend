package com.nexoralabs.flowtrack.inventory.domain.model.queries;

public record GetProductByBarcodeQuery(String barcode) {
    public GetProductByBarcodeQuery {
        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException("barcode cannot be null or blank");
        }
    }
}
