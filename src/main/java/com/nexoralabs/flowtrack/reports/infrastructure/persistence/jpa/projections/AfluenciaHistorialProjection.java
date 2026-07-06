package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections;

public interface AfluenciaHistorialProjection {
    String getFecha();
    String getDiaSemana();
    Integer getHora();
    String getCamaraId();
    Integer getTotalIngresos();
}
