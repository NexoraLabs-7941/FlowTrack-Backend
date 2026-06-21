package com.nexoralabs.flowtrack.integration.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity representing persisted system or user notifications.
 */
@Entity
@Table(name = "stored_notifications")
@Getter
@Setter
@NoArgsConstructor
public class StoredNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false, length = 1024)
    private String body;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // e.g. "warning", "alert", "info"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StoredNotification(String userId, String title, String body, String type) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type != null ? type : "info";
        this.createdAt = LocalDateTime.now();
    }
}
