package com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.integration.domain.model.entities.StoredNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for managing StoredNotification entity persistence.
 */
@Repository
public interface StoredNotificationRepository extends JpaRepository<StoredNotification, Long> {
    /**
     * Find all notifications stored for a specific user, ordered by creation date descending.
     * @param userId Target user ID
     * @return List of persisted notifications
     */
    List<StoredNotification> findByUserIdOrderByCreatedAtDesc(String userId);
}
