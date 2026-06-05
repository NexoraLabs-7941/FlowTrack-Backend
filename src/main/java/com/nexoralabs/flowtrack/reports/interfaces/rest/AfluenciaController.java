package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.infrastructure.dto.VisionDetectionDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/afluencia")
public class AfluenciaController {

    // Mapa para guardar la última detección por dispositivo_id
    private final ConcurrentHashMap<String, Integer> cacheAfluencia = new ConcurrentHashMap<>();

    // 1. ESCUCHADOR DE KAFKA
    // Nota: Configura tu Kafka para usar JsonDeserializer en lugar de StringDeserializer
    @KafkaListener(topics = "bodega.eventos.personas", groupId = "flowtrack-group")
    public void recibirDeteccion(VisionDetectionDTO mensaje) {
        System.out.println("🚨 [DEBUG] ¡El listener de Kafka se ha activado!");

        if (mensaje == null) {
            System.err.println("❌ [DEBUG] El mensaje recibido es NULO");
            return;
        }

        System.out.println("📦 [DEBUG] Mensaje recibido: " + mensaje.dispositivo_id);

        if (mensaje.detecciones != null) {
            int conteo = mensaje.detecciones.getOrDefault("person", 0);
            cacheAfluencia.put(mensaje.dispositivo_id, conteo);
            System.out.println("✅ [DEBUG] Cache actualizado con: " + conteo + " personas para " + mensaje.dispositivo_id);
        } else {
            System.err.println("❌ [DEBUG] El campo 'detecciones' es nulo");
        }
    }

    // 2. ENDPOINT PARA EL FRONTEND
    @GetMapping("/{idCamara}")
    public Integer obtenerAfluencia(@PathVariable String idCamara) {
        return cacheAfluencia.getOrDefault(idCamara, 0);
    }
}