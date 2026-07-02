package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RestockDetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestockDetectionRecordRepository extends JpaRepository<RestockDetectionRecord, Long> {

    List<RestockDetectionRecord> findAllByOrderByCreatedAtDesc();
}
