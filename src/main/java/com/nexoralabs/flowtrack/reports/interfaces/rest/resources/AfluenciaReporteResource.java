package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

import java.util.List;

public record AfluenciaReporteResource(
        List<AfluenciaAgrupadaResource> traficoDiario,
        List<AfluenciaAgrupadaResource> horasPico
) {
}
