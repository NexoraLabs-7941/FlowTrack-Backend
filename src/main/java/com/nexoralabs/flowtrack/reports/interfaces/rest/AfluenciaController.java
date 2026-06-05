package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.infrastructure.dto.VisionDetectionDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/afluencia")
@CrossOrigin(origins = "*") // <-- Crucial para que el Front pueda leer los datos sin bloqueos de CORS
public class AfluenciaController {

    // 1. CORREGIDO: Cambiado a String para soportar los identificadores del Edge AI
    private final ConcurrentHashMap<String, Integer> cacheAfluencia = new ConcurrentHashMap<>();

    // ESCUCHADOR DE KAFKA
    @KafkaListener(topics = "bodega.eventos.personas", groupId = "flowtrack-group-v2") // <-- Asegúrate de usar el grupo v2 o superior
    public void recibirDeteccion(VisionDetectionDTO mensaje) {
        System.out.println("🚨 [DEBUG] ¡El listener de Kafka se ha activado!");

        if (mensaje == null) {
            System.err.println("❌ [DEBUG] El mensaje recibido es NULO");
            return;
        }

        System.out.println("📦 [DEBUG] Mensaje recibido del dispositivo: " + mensaje.dispositivo_id);

        if (mensaje.detecciones != null) {
            int conteo = mensaje.detecciones.getOrDefault("person", 0);

            // Guardamos usando el String exacto (ej. "camara_0")
            cacheAfluencia.put(mensaje.dispositivo_id, conteo);
            System.out.println("✅ [DEBUG] Cache actualizado con: " + conteo + " personas para " + mensaje.dispositivo_id);
        } else {
            System.err.println("❌ [DEBUG] El campo 'detecciones' es nulo");
        }
    }

    // ENDPOINT PARA EL FRONTEND
    @GetMapping("/{idCamara}")
    public Integer obtenerAfluencia(@PathVariable String idCamara) {
        // 2. CORREGIDO: Python envía los eventos con el prefijo "camara_" (ej: "camara_0")
        // Construimos la misma clave para que el getOrDefault lo encuentre con éxito
        String llaveBusqueda = "camara_" + idCamara;
        return cacheAfluencia.getOrDefault(llaveBusqueda, 0);
    }
}