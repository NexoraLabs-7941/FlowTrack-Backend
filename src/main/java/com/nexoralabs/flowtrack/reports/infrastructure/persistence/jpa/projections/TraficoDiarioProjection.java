package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections;

public interface TraficoDiarioProjection {
    String getFecha();
    String getDiaSemana();
    Integer getTotalIngresos();
}
