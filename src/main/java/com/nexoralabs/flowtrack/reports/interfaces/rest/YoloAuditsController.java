package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.domain.model.entities.YoloAudit;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.YoloAuditRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/yolo-audits")
@Tag(name = "YOLO Audits", description = "Endpoints for YOLO audits")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class YoloAuditsController {

    private final YoloAuditRepository repository;

    public YoloAuditsController(YoloAuditRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<YoloAudit>> getAllAudits() {
        return ResponseEntity.ok(repository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<YoloAudit> createAudit(@RequestBody YoloAudit audit) {
        return ResponseEntity.ok(repository.save(audit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<YoloAudit> updateAudit(@PathVariable String id, @RequestBody YoloAudit audit) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.save(audit));
    }
}
