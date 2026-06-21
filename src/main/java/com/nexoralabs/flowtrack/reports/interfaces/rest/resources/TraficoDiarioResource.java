package com.nexoralabs.flowtrack.reports.interfaces.rest.resources;

public record TraficoDiarioResource(
        String fecha,
        String diaSemana,
        Integer totalIngresos
) {
}
