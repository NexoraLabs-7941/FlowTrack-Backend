package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.application.RestockDetectionRecordService;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.RestockDetectionRecordResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.VisionDetectionCountResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
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
    private final RestockDetectionRecordService restockDetectionRecordService;

    public RestockDetectionController(
            RestTemplate restTemplate,
            @Value("${edge.vision.detect-image-url:http://localhost:8000/api/v1/vision/detect-image}")
            String pythonVisionDetectImageUrl,
            RestockDetectionRecordService restockDetectionRecordService) {
        this.restTemplate = restTemplate;
        this.pythonVisionDetectImageUrl = pythonVisionDetectImageUrl;
        this.restockDetectionRecordService = restockDetectionRecordService;
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

    @PostMapping(value = "/deteccion/registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Guardar ingreso de stock con foto YOLO",
            description = "Sube la imagen a Cloudinary, crea el batch de inventario y persiste el registro con lote, fechas y cantidades"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro guardado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error al subir imagen o guardar registro")
    })
    public ResponseEntity<?> guardarRegistro(
            @RequestParam("image") MultipartFile image,
            @RequestParam("lote") String lote,
            @RequestParam("receptionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receptionDate,
            @RequestParam("expirationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expirationDate,
            @RequestParam("productId") Long productId,
            @RequestParam("detectedQuantity") Integer detectedQuantity,
            @RequestParam("verifiedQuantity") Integer verifiedQuantity) {
        try {
            RestockDetectionRecordResource saved = restockDetectionRecordService.saveRecord(
                    image,
                    lote,
                    receptionDate,
                    expirationDate,
                    productId,
                    detectedQuantity,
                    verifiedQuantity
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "No se pudo guardar el registro: " + e.getMessage()));
        }
    }

    @GetMapping("/deteccion/registros")
    @Operation(
            summary = "Listar registros de ingreso YOLO",
            description = "Obtiene todos los registros guardados con lote, fechas, URL de imagen y cantidades"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<RestockDetectionRecordResource>> listarRegistros() {
        return ResponseEntity.ok(restockDetectionRecordService.getAllRecords());
    }
}
