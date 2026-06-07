package com.nexoralabs.flowtrack.reports.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

// Clase principal que representa el JSON
public class VisionDetectionDTO {
    @JsonProperty("dispositivo_id")
    public String dispositivo_id;
    public String timestamp;
    public Map<String, Integer> detecciones; // Esto mapea "detecciones": {"person": 0}
}