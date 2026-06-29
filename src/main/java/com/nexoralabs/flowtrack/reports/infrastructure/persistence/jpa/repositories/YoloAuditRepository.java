package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.YoloAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoloAuditRepository extends JpaRepository<YoloAudit, String> {
    List<YoloAudit> findAllByOrderByCreatedAtDesc();
}
