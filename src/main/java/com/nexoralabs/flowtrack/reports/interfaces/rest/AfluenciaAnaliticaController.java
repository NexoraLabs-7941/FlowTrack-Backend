package com.nexoralabs.flowtrack.reports.interfaces.rest;

import com.nexoralabs.flowtrack.reports.application.AfluenciaAnaliticaService;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.AfluenciaHistorialResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.HorasPicoResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.TraficoDiarioResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/analitica", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analitica de afluencia", description = "Consultas historicas filtrables de afluencia")
@SecurityRequirement(name = "bearerAuth")
public class AfluenciaAnaliticaController {

    private final AfluenciaAnaliticaService afluenciaAnaliticaService;

    public AfluenciaAnaliticaController(AfluenciaAnaliticaService afluenciaAnaliticaService) {
        this.afluenciaAnaliticaService = afluenciaAnaliticaService;
    }

    @GetMapping("/camaras")
    @Operation(
            summary = "Listar camaras de afluencia",
            description = "Devuelve los IDs de camara encontrados en los registros historicos de afluencia"
    )
    public ResponseEntity<List<String>> obtenerCamarasDisponibles() {
        return ResponseEntity.ok(afluenciaAnaliticaService.obtenerCamarasDisponibles());
    }

    @GetMapping("/trafico-diario")
    @Operation(
            summary = "Obtener trafico diario",
            description = "Agrupa los ingresos por fecha y permite filtrar por camara y rango de fechas"
    )
    public ResponseEntity<List<TraficoDiarioResource>> obtenerTraficoDiario(
            @RequestParam(required = false) String camaraId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(afluenciaAnaliticaService.obtenerTraficoDiario(camaraId, fechaInicio, fechaFin));
    }

    @GetMapping("/horas-pico")
    @Operation(
            summary = "Obtener horas pico",
            description = "Agrupa los ingresos por hora para una camara y fecha especifica"
    )
    public ResponseEntity<List<HorasPicoResource>> obtenerHorasPico(
            @RequestParam String camaraId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(afluenciaAnaliticaService.obtenerHorasPico(camaraId, fecha));
    }

    @GetMapping("/historial")
    @Operation(
            summary = "Obtener historial de afluencia",
            description = "Agrupa los ingresos historicos por dia, hora y camara con filtros opcionales"
    )
    public ResponseEntity<List<AfluenciaHistorialResource>> obtenerHistorial(
            @RequestParam(required = false) String camaraId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(afluenciaAnaliticaService.obtenerHistorial(
                camaraId,
                fechaInicio,
                fechaFin));
    }

    @GetMapping(value = "/historial/export", produces = "text/csv")
    @Operation(
            summary = "Exportar historial de afluencia",
            description = "Exporta en CSV los ingresos agrupados por dia, hora y camara segun los filtros enviados"
    )
    public ResponseEntity<byte[]> exportarHistorial(
            @RequestParam(required = false) String camaraId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        byte[] csv = afluenciaAnaliticaService.exportarHistorialCsv(camaraId, fechaInicio, fechaFin);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=afluencia-historial.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
