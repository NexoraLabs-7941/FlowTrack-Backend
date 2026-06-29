package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.dto.VisionDetectionDTO;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.RegistroAfluenciaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/afluencia")
@CrossOrigin(origins = "*")
public class AfluenciaController {

    private final ObjectMapper objectMapper;
    private final RegistroAfluenciaRepository registroAfluenciaRepository;
    private final ConcurrentHashMap<String, Integer> cacheAfluencia = new ConcurrentHashMap<>();

    public AfluenciaController(ObjectMapper objectMapper, RegistroAfluenciaRepository registroAfluenciaRepository) {
        this.objectMapper = objectMapper;
        this.registroAfluenciaRepository = registroAfluenciaRepository;
    }

    @KafkaListener(topics = "flowtrack-detecciones-afluencia", groupId = "flowtrack-group")
    public void recibirDeteccion(String payload) throws JsonProcessingException {
        VisionDetectionDTO mensaje = objectMapper.readValue(payload, VisionDetectionDTO.class);

        if (mensaje == null || mensaje.detecciones == null) {
            return;
        }

        int nuevoConteo = mensaje.detecciones.getOrDefault("person", 0);
        int conteoAnterior = cacheAfluencia.getOrDefault(mensaje.dispositivo_id, 0);
        
        cacheAfluencia.put(mensaje.dispositivo_id, nuevoConteo);

        int delta = nuevoConteo - conteoAnterior;

        if (delta > 0) {
            registroAfluenciaRepository.save(new RegistroAfluencia(
                    parseTimestamp(mensaje.timestamp),
                    delta,
                    "ingreso",
                    mensaje.dispositivo_id
            ));
        } else if (delta < 0) {
            registroAfluenciaRepository.save(new RegistroAfluencia(
                    parseTimestamp(mensaje.timestamp),
                    Math.abs(delta),
                    "salida",
                    mensaje.dispositivo_id
            ));
        }
    }

    @GetMapping("/{idCamara}")
    public Integer obtenerAfluencia(@PathVariable String idCamara) {
        // Retornamos la afluencia global sumando todas las cámaras activas
        return cacheAfluencia.values().stream().mapToInt(Integer::intValue).sum();
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            return OffsetDateTime.parse(timestamp).toLocalDateTime();
        } catch (Exception ignored) {
            try {
                return LocalDateTime.parse(timestamp);
            } catch (Exception ignoredAgain) {
                return LocalDateTime.now();
            }
        }
    }
}
