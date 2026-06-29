package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.PeopleCounterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleCounterRecordRepository extends JpaRepository<PeopleCounterRecord, String> {
    List<PeopleCounterRecord> findTop20ByOrderByDateDescHourDesc();
}
