package com.nexoralabs.flowtrack.reports.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "yolo_detections")
@Getter
@Setter
public class YoloDetection {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "confidence")
    private double confidence;

    @Embedded
    private YoloBox box;
}
