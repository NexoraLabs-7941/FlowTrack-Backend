package com.nexoralabs.flowtrack.integration.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity representing registered user devices and FCM tokens.
 */
@Entity
@Table(name = "user_devices")
@Getter
@Setter
@NoArgsConstructor
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "firebase_token", nullable = false, unique = true, length = 512)
    private String firebaseToken;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public UserDevice(String userId, String firebaseToken, String deviceType) {
        this.userId = userId;
        this.firebaseToken = firebaseToken;
        this.deviceType = deviceType != null ? deviceType : "web";
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
