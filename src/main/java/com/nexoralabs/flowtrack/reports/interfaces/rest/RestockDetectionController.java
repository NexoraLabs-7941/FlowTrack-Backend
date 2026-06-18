package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.VisionDetectionCountResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Orquestador de detección YOLO para reposición de inventario.
 * El frontend envía una imagen y Java delega la inferencia en Edge Vision (Python).
 */
@RestController
@RequestMapping(value = "/api/v1/inventario", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inventario YOLO", description = "Detección de objetos para reposición de inventario vía Edge Vision")
@SecurityRequirement(name = "bearerAuth")
public class RestockDetectionController {

    private final RestTemplate restTemplate;
    private final String pythonVisionDetectImageUrl;

    public RestockDetectionController(
            RestTemplate restTemplate,
            @Value("${edge.vision.detect-image-url:http://localhost:8000/api/v1/vision/detect-image}")
            String pythonVisionDetectImageUrl) {
        this.restTemplate = restTemplate;
        this.pythonVisionDetectImageUrl = pythonVisionDetectImageUrl;
    }

    @PostMapping(value = "/deteccion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Detectar objetos en imagen de reposición",
            description = "Envía una imagen a Edge Vision y devuelve el conteo de objetos detectados por YOLO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detección realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Imagen inválida o vacía"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error al conectar con Edge Vision")
    })
    public ResponseEntity<?> detectarObjetos(@RequestParam("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "La imagen es obligatoria."));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    String original = image.getOriginalFilename();
                    return original != null && !original.isBlank() ? original : "capture.jpg";
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<VisionDetectionCountResource> response = restTemplate.postForEntity(
                    pythonVisionDetectImageUrl,
                    requestEntity,
                    VisionDetectionCountResource.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al conectar con Edge Vision para detección de inventario"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "No se pudo procesar la imagen: " + e.getMessage()));
        }
    }
}
