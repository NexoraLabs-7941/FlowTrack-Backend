package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta del microservicio Edge Vision (Python) reenviada al frontend.
 */
public record VisionStreamResource(
        String status,
        String message,
        @JsonProperty("stream_url") String streamUrl
) {
}
