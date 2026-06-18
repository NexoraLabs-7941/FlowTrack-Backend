package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Respuesta del microservicio Edge Vision para detección de objetos en inventario.
 */
public record VisionDetectionCountResource(
        String status,
        @JsonProperty("total_count") int totalCount,
        Map<String, Integer> detections,
        double confidence,
        @JsonProperty("annotated_image_base64") String annotatedImageBase64
) {
}
