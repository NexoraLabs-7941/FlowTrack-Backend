package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoralabs.flowtrack.reports.infrastructure.dto.VisionDetectionDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/afluencia")
@CrossOrigin(origins = "*")
public class AfluenciaController {

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Integer> cacheAfluencia = new ConcurrentHashMap<>();

    public AfluenciaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "flowtrack-detecciones-afluencia", groupId = "flowtrack-group")
    public void recibirDeteccion(String payload) throws JsonProcessingException {
        VisionDetectionDTO mensaje = objectMapper.readValue(payload, VisionDetectionDTO.class);

        if (mensaje == null || mensaje.detecciones == null) {
            return;
        }

        int conteo = mensaje.detecciones.getOrDefault("person", 0);
        cacheAfluencia.put(mensaje.dispositivo_id, conteo);
    }

    @GetMapping("/{idCamara}")
    public Integer obtenerAfluencia(@PathVariable String idCamara) {
        String llaveBusqueda = "camara_" + idCamara;
        return cacheAfluencia.getOrDefault(llaveBusqueda, 0);
    }
}
