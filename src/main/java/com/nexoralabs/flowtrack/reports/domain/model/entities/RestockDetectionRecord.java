package com.nexoralabs.flowtrack.reports.domain.model.entities;

import com.nexoralabs.flowtrack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "restock_detection_records")
@Getter
@NoArgsConstructor
public class RestockDetectionRecord extends AuditableAbstractAggregateRoot<RestockDetectionRecord> {

    @Column(nullable = false)
    private String lote;

    @Column(nullable = false)
    private Date receptionDate;

    @Column(nullable = false)
    private Date expirationDate;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer detectedQuantity;

    @Column(nullable = false)
    private Integer verifiedQuantity;

    @Column(nullable = false)
    private Long productId;

    @Column
    private Long batchId;

    public RestockDetectionRecord(
            String lote,
            Date receptionDate,
            Date expirationDate,
            String imageUrl,
            Integer detectedQuantity,
            Integer verifiedQuantity,
            Long productId,
            Long batchId) {
        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("lote is required");
        }
        if (receptionDate == null) {
            throw new IllegalArgumentException("receptionDate is required");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("expirationDate is required");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl is required");
        }
        if (detectedQuantity == null || detectedQuantity < 0) {
            throw new IllegalArgumentException("detectedQuantity cannot be null or negative");
        }
        if (verifiedQuantity == null || verifiedQuantity < 0) {
            throw new IllegalArgumentException("verifiedQuantity cannot be null or negative");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId is required");
        }

        this.lote = lote.trim();
        this.receptionDate = receptionDate;
        this.expirationDate = expirationDate;
        this.imageUrl = imageUrl;
        this.detectedQuantity = detectedQuantity;
        this.verifiedQuantity = verifiedQuantity;
        this.productId = productId;
        this.batchId = batchId;
    }
}
