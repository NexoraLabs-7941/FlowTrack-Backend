package com.nexoralabs.flowtrack.reports.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "yolo_audits")
@Getter
@Setter
public class YoloAudit {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "status")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id")
    private List<YoloDetection> detections;
}
