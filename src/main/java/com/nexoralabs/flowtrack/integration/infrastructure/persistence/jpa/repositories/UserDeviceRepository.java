package com.nexoralabs.flowtrack.integration.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.integration.domain.model.entities.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for managing UserDevice entity persistence.
 */
@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    /**
     * Find a user device by its unique Firebase token.
     * @param firebaseToken Unique Firebase registration token
     * @return Optional containing the UserDevice if found
     */
    Optional<UserDevice> findByFirebaseToken(String firebaseToken);

    /**
     * Find all active devices and tokens registered by a user ID.
     * @param userId Target user ID
     * @return List of active UserDevices
     */
    List<UserDevice> findByUserId(String userId);
}
