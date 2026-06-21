package com.nexoralabs.flowtrack.reports.application;

import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.HorasPicoResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.TraficoDiarioResource;

import java.time.LocalDate;
import java.util.List;

public interface AfluenciaAnaliticaService {
    List<TraficoDiarioResource> obtenerTraficoDiario(String camaraId, LocalDate fechaInicio, LocalDate fechaFin);

    List<HorasPicoResource> obtenerHorasPico(String camaraId, LocalDate fecha);
}
