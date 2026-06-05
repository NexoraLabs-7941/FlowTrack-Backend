package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.VisionStreamResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Orquestador de aforo perimetral. El frontend habla con Java (IAM) y Java delega en Edge Vision (Python).
 */
@RestController
@RequestMapping(value = "/api/v1/aforo", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Aforo", description = "Control del streaming perimetral de aforo vía Edge Vision")
@SecurityRequirement(name = "bearerAuth")
public class AforoController {

    private final RestTemplate restTemplate;
    private final String pythonVisionStartUrl;
    private final String pythonVisionStopUrl;

    public AforoController(
            RestTemplate restTemplate,
            @Value("${edge.vision.start-url:http://localhost:8000/api/v1/vision/start}") String pythonVisionStartUrl,
            @Value("${edge.vision.stop-url:http://localhost:8000/api/v1/vision/stop}") String pythonVisionStopUrl) {
        this.restTemplate = restTemplate;
        this.pythonVisionStartUrl = pythonVisionStartUrl;
        this.pythonVisionStopUrl = pythonVisionStopUrl;
    }

    @PostMapping("/encender/{idCamara}")
    @Operation(
            summary = "Activar cámara de aforo",
            description = "Inicia el streaming perimetral en Edge Vision y devuelve la URL HLS para el frontend"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cámara activada correctamente"),
            @ApiResponse(responseCode = "400", description = "El streaming ya está activo o no se pudo iniciar"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error al conectar con Edge Vision")
    })
    public ResponseEntity<?> activarCamaraAforo(@PathVariable int idCamara) {
        try {
            String urlDestino = pythonVisionStartUrl + "?camera_id=" + idCamara;
            ResponseEntity<VisionStreamResource> response =
                    restTemplate.postForEntity(urlDestino, null, VisionStreamResource.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al conectar con el componente perimetral Edge Vision"));
        }
    }

    @PostMapping("/apagar")
    @Operation(
            summary = "Detener cámara de aforo",
            description = "Detiene el streaming perimetral activo en Edge Vision"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streaming detenido correctamente"),
            @ApiResponse(responseCode = "400", description = "No hay streaming activo"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error al conectar con Edge Vision")
    })
    public ResponseEntity<?> detenerCamaraAforo() {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonVisionStopUrl, null, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al conectar con el componente perimetral Edge Vision"));
        }
    }
}
