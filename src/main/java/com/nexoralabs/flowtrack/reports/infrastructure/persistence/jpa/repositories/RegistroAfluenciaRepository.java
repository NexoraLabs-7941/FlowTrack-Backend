package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.AfluenciaAgrupadaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistroAfluenciaRepository extends JpaRepository<RegistroAfluencia, Long> {

    @Query(value = """
            SELECT
                TRIM(TO_CHAR(fecha_hora, 'Day')) AS periodo,
                COALESCE(SUM(cantidad), 0) AS total
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
            GROUP BY EXTRACT(ISODOW FROM fecha_hora), TRIM(TO_CHAR(fecha_hora, 'Day'))
            ORDER BY EXTRACT(ISODOW FROM fecha_hora)
            """, nativeQuery = true)
    List<AfluenciaAgrupadaProjection> obtenerTraficoDiario();

    @Query(value = """
            SELECT
                LPAD(CAST(EXTRACT(HOUR FROM fecha_hora) AS TEXT), 2, '0') || ':00' AS periodo,
                COALESCE(SUM(cantidad), 0) AS total
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
            GROUP BY EXTRACT(HOUR FROM fecha_hora)
            ORDER BY EXTRACT(HOUR FROM fecha_hora)
            """, nativeQuery = true)
    List<AfluenciaAgrupadaProjection> obtenerHorasPico();
}
