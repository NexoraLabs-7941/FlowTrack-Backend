package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.application.AfluenciaReporteService;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.AfluenciaReporteResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/reportes/afluencia", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reportes de afluencia", description = "Analitica historica de eventos de afluencia")
@SecurityRequirement(name = "bearerAuth")
public class ReportesAfluenciaController {

    private final AfluenciaReporteService afluenciaReporteService;

    public ReportesAfluenciaController(AfluenciaReporteService afluenciaReporteService) {
        this.afluenciaReporteService = afluenciaReporteService;
    }

    @GetMapping
    @Operation(
            summary = "Obtener reporte historico de afluencia",
            description = "Agrupa los ingresos por dia de la semana y por hora del dia para graficos historicos"
    )
    public ResponseEntity<AfluenciaReporteResource> obtenerReporteAfluencia() {
        return ResponseEntity.ok(afluenciaReporteService.obtenerReporteAfluencia());
    }
}
