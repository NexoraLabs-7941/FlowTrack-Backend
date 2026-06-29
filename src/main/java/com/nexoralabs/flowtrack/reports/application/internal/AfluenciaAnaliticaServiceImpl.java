package com.nexoralabs.flowtrack.reports.application.internal;

import com.nexoralabs.flowtrack.reports.application.AfluenciaAnaliticaService;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.HorasPicoProjection;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.AfluenciaRegistroRepository;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.HorasPicoResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.TraficoDiarioResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AfluenciaAnaliticaServiceImpl implements AfluenciaAnaliticaService {

    private static final LocalDate FECHA_INICIO_HISTORICA = LocalDate.of(1900, 1, 1);
    private static final LocalDate FECHA_FIN_HISTORICA = LocalDate.of(9999, 12, 31);

    private final AfluenciaRegistroRepository afluenciaRegistroRepository;

    public AfluenciaAnaliticaServiceImpl(AfluenciaRegistroRepository afluenciaRegistroRepository) {
        this.afluenciaRegistroRepository = afluenciaRegistroRepository;
    }

    @Override
    public List<TraficoDiarioResource> obtenerTraficoDiario(
            String camaraId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {
        validarRangoFechas(fechaInicio, fechaFin);

        LocalDateTime inicio = (fechaInicio != null ? fechaInicio : FECHA_INICIO_HISTORICA).atStartOfDay();
        LocalDateTime finExclusive = (fechaFin != null ? fechaFin : FECHA_FIN_HISTORICA).plusDays(1).atStartOfDay();

        // Ignoramos el camaraId para que devuelva el tráfico diario global de todas las cámaras combinadas
        return afluenciaRegistroRepository.obtenerTraficoDiario(null, inicio, finExclusive)
                .stream()
                .map(item -> new TraficoDiarioResource(
                        item.getFecha(),
                        item.getDiaSemana(),
                        item.getTotalIngresos()))
                .toList();
    }

    @Override
    public List<HorasPicoResource> obtenerHorasPico(String camaraId, LocalDate fecha) {
        if (camaraId == null || camaraId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "camaraId es requerido");
        }
        if (fecha == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecha es requerida");
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDiaExclusive = fecha.plusDays(1).atStartOfDay();

        Map<Integer, HorasPicoProjection> horasConDatos = afluenciaRegistroRepository
                .obtenerHorasPico(normalizarCamaraId(camaraId), inicioDia, finDiaExclusive)
                .stream()
                .collect(Collectors.toMap(HorasPicoProjection::getHora, Function.identity()));

        return IntStream.rangeClosed(0, 23)
                .mapToObj(hora -> {
                    HorasPicoProjection item = horasConDatos.get(hora);
                    Integer total = item != null ? item.getTotalIngresos() : 0;
                    return new HorasPicoResource(hora, total);
                })
                .toList();
    }

    private void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio no puede ser posterior a fechaFin");
        }
    }

    private String normalizarCamaraId(String camaraId) {
        if (camaraId == null || camaraId.isBlank()) {
            return null;
        }

        String camaraIdNormalizado = camaraId.trim();
        
        // Fix para unificar IDs que vienen como "camara 0" en lugar de "camara_0" o "0"
        if (camaraIdNormalizado.toLowerCase().startsWith("camara ")) {
            camaraIdNormalizado = camaraIdNormalizado.replaceAll("(?i)camara ", "camara_");
        } else if (camaraIdNormalizado.matches("\\d+")) {
            camaraIdNormalizado = "camara_" + camaraIdNormalizado;
        }

        return camaraIdNormalizado;
    }
}
