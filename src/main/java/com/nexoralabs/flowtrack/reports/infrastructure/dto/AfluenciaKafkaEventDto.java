package com.nexoralabs.flowtrack.reports.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AfluenciaKafkaEventDto(
        String evento,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        @JsonProperty("camara_id")
        String camaraId,
        Integer cantidad
) {
}
