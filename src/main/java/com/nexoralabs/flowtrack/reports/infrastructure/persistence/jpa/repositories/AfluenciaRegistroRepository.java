package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.HorasPicoProjection;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.TraficoDiarioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AfluenciaRegistroRepository extends JpaRepository<RegistroAfluencia, Long> {

    @Query(value = """
            SELECT
                DATE_FORMAT(fecha_hora, '%Y-%m-%d') AS fecha,
                CASE WEEKDAY(fecha_hora)
                    WHEN 0 THEN 'Lunes'
                    WHEN 1 THEN 'Martes'
                    WHEN 2 THEN 'Miercoles'
                    WHEN 3 THEN 'Jueves'
                    WHEN 4 THEN 'Viernes'
                    WHEN 5 THEN 'Sabado'
                    WHEN 6 THEN 'Domingo'
                END AS diaSemana,
                CAST(COALESCE(SUM(cantidad), 0) AS SIGNED) AS totalIngresos
            FROM registros_afluencia
            WHERE LOWER(tipo_movimiento) = 'ingreso'
              AND (:camaraId IS NULL OR camara_id = :camaraId)
              AND fecha_hora >= :fechaInicio
              AND fecha_hora < :fechaFinExclusive
            GROUP BY DATE(fecha_hora), WEEKDAY(fecha_hora)
            ORDER BY DATE(fecha_hora)
            """, nativeQuery = true)
    List<TraficoDiarioProjection> obtenerTraficoDiario(
            @Param("camaraId") String camaraId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFinExclusive") LocalDateTime fechaFinExclusive);

    @Query(value = """
            SELECT
                HOUR(fecha_hora) AS hora,
                CAST(COALESCE(SUM(cantidad), 0) AS SIGNED) AS totalIngresos
            FROM registros_afluencia
            WHERE LOWER(tipo_movimiento) = 'ingreso'
              AND camara_id = :camaraId
              AND fecha_hora >= :inicioDia
              AND fecha_hora < :finDiaExclusive
            GROUP BY HOUR(fecha_hora)
            ORDER BY HOUR(fecha_hora)
            """, nativeQuery = true)
    List<HorasPicoProjection> obtenerHorasPico(
            @Param("camaraId") String camaraId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDiaExclusive") LocalDateTime finDiaExclusive);
}
