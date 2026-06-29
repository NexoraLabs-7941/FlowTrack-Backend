package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.domain.model.entities.PeopleCounterRecord;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.PeopleCounterRecordRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people-counters")
@Tag(name = "People Counters", description = "Endpoints for people counter records")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class PeopleCountersController {

    private final PeopleCounterRecordRepository repository;

    public PeopleCountersController(PeopleCounterRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<PeopleCounterRecord>> getAllRecords() {
        return ResponseEntity.ok(repository.findTop20ByOrderByDateDescHourDesc());
    }

    @PostMapping
    public ResponseEntity<PeopleCounterRecord> createRecord(@RequestBody PeopleCounterRecord record) {
        return ResponseEntity.ok(repository.save(record));
    }
}
