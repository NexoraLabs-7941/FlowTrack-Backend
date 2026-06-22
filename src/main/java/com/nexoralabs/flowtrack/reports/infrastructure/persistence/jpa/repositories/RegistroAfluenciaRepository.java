package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.AfluenciaAgrupadaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistroAfluenciaRepository extends JpaRepository<RegistroAfluencia, Long> {

    @Query(value = """
            SELECT
                CASE WEEKDAY(fecha_hora)
                    WHEN 0 THEN 'Lunes'
                    WHEN 1 THEN 'Martes'
                    WHEN 2 THEN 'Miercoles'
                    WHEN 3 THEN 'Jueves'
                    WHEN 4 THEN 'Viernes'
                    WHEN 5 THEN 'Sabado'
                    WHEN 6 THEN 'Domingo'
                END AS periodo,
                CAST(COALESCE(SUM(cantidad), 0) AS SIGNED) AS total
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
            GROUP BY WEEKDAY(fecha_hora)
            ORDER BY WEEKDAY(fecha_hora)
            """, nativeQuery = true)
    List<AfluenciaAgrupadaProjection> obtenerTraficoDiario();

    @Query(value = """
            SELECT
                CONCAT(LPAD(HOUR(fecha_hora), 2, '0'), ':00') AS periodo,
                CAST(COALESCE(SUM(cantidad), 0) AS SIGNED) AS total
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
            GROUP BY HOUR(fecha_hora)
            ORDER BY HOUR(fecha_hora)
            """, nativeQuery = true)
    List<AfluenciaAgrupadaProjection> obtenerHorasPico();
}
