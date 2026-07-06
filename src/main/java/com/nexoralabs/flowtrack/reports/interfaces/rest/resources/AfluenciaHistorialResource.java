package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

public record AfluenciaHistorialResource(
        String fecha,
        String diaSemana,
        Integer hora,
        String horaInicio,
        String horaFin,
        String rangoHora,
        String camaraId,
        Integer totalIngresos
) {
}
