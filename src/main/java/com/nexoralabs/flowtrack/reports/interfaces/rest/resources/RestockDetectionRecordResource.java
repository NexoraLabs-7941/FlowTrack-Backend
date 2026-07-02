package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RestockDetectionRecord;

import java.util.Date;

public record RestockDetectionRecordResource(
        Long id,
        String lote,
        Date receptionDate,
        Date expirationDate,
        String imageUrl,
        Integer detectedQuantity,
        Integer verifiedQuantity,
        Long productId,
        Long batchId,
        Date createdAt
) {
    public static RestockDetectionRecordResource fromEntity(RestockDetectionRecord entity) {
        return new RestockDetectionRecordResource(
                entity.getId(),
                entity.getLote(),
                entity.getReceptionDate(),
                entity.getExpirationDate(),
                entity.getImageUrl(),
                entity.getDetectedQuantity(),
                entity.getVerifiedQuantity(),
                entity.getProductId(),
                entity.getBatchId(),
                entity.getCreatedAt()
        );
    }
}
