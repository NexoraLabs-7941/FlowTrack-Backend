package com.nexoralabs.flowtrack.reports.application;

import com.nexoralabs.flowtrack.inventory.domain.model.commands.CreateBatchCommand;
import com.nexoralabs.flowtrack.inventory.domain.services.BatchCommandService;
import com.nexoralabs.flowtrack.reports.domain.model.entities.RestockDetectionRecord;
import com.nexoralabs.flowtrack.reports.infrastructure.cloudinary.CloudinaryService;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.RestockDetectionRecordRepository;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.RestockDetectionRecordResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RestockDetectionRecordService {

    private final CloudinaryService cloudinaryService;
    private final BatchCommandService batchCommandService;
    private final RestockDetectionRecordRepository repository;

    public RestockDetectionRecordService(
            CloudinaryService cloudinaryService,
            BatchCommandService batchCommandService,
            RestockDetectionRecordRepository repository) {
        this.cloudinaryService = cloudinaryService;
        this.batchCommandService = batchCommandService;
        this.repository = repository;
    }

    @Transactional
    public RestockDetectionRecordResource saveRecord(
            MultipartFile image,
            String lote,
            LocalDate receptionDate,
            LocalDate expirationDate,
            Long productId,
            Integer detectedQuantity,
            Integer verifiedQuantity) throws IOException {

        if (verifiedQuantity == null || verifiedQuantity <= 0) {
            throw new IllegalArgumentException("verifiedQuantity debe ser mayor a 0.");
        }

        String imageUrl = cloudinaryService.uploadImage(image);

        Date reception = Date.from(receptionDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date expiration = Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Long batchId = batchCommandService.handle(new CreateBatchCommand(
                productId,
                verifiedQuantity,
                expiration,
                reception
        ));

        RestockDetectionRecord record = new RestockDetectionRecord(
                lote,
                reception,
                expiration,
                imageUrl,
                detectedQuantity != null ? detectedQuantity : 0,
                verifiedQuantity,
                productId,
                batchId
        );

        RestockDetectionRecord saved = repository.save(record);
        return RestockDetectionRecordResource.fromEntity(saved);
    }

    public List<RestockDetectionRecordResource> getAllRecords() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(RestockDetectionRecordResource::fromEntity)
                .toList();
    }

    @Transactional
    public Optional<RestockDetectionRecordResource> updateRecordImage(
            Long id,
            MultipartFile image,
            String imageUrl) throws IOException {

        Optional<RestockDetectionRecord> recordOpt = repository.findById(id);
        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        String newImageUrl;
        if (image != null && !image.isEmpty()) {
            newImageUrl = cloudinaryService.uploadImage(image);
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            newImageUrl = imageUrl.trim();
        } else {
            throw new IllegalArgumentException("Debe enviar una imagen o una imageUrl.");
        }

        RestockDetectionRecord record = recordOpt.get();
        record.updateImageUrl(newImageUrl);
        return Optional.of(RestockDetectionRecordResource.fromEntity(repository.save(record)));
    }
}
