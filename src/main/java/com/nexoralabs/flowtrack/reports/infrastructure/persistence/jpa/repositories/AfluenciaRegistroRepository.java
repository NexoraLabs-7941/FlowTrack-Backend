package com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.AfluenciaHistorialProjection;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.HorasPicoProjection;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.TraficoDiarioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AfluenciaRegistroRepository extends JpaRepository<RegistroAfluencia, Long> {

    @Query("""
            SELECT DISTINCT r.camaraId
            FROM RegistroAfluencia r
            ORDER BY r.camaraId
            """)
    List<String> obtenerCamarasDisponibles();

    @Query(value = """
            SELECT
                TO_CHAR(CAST(fecha_hora AS DATE), 'YYYY-MM-DD') AS fecha,
                CASE EXTRACT(ISODOW FROM fecha_hora)
                    WHEN 1 THEN 'Lunes'
                    WHEN 2 THEN 'Martes'
                    WHEN 3 THEN 'Miercoles'
                    WHEN 4 THEN 'Jueves'
                    WHEN 5 THEN 'Viernes'
                    WHEN 6 THEN 'Sabado'
                    WHEN 7 THEN 'Domingo'
                END AS "diaSemana",
                CAST(EXTRACT(HOUR FROM fecha_hora) AS INTEGER) AS hora,
                camara_id AS "camaraId",
                CAST(COALESCE(SUM(cantidad), 0) AS INTEGER) AS "totalIngresos"
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
              AND (:camaraId IS NULL OR camara_id = :camaraId)
              AND fecha_hora >= CAST(:fechaInicio AS TIMESTAMP)
              AND fecha_hora < CAST(:fechaFinExclusive AS TIMESTAMP)
            GROUP BY CAST(fecha_hora AS DATE), EXTRACT(ISODOW FROM fecha_hora), EXTRACT(HOUR FROM fecha_hora), camara_id
            ORDER BY CAST(fecha_hora AS DATE) DESC, EXTRACT(HOUR FROM fecha_hora) DESC, camara_id
            """, nativeQuery = true)
    List<AfluenciaHistorialProjection> obtenerHistorialAgrupadoPorHora(
            @Param("camaraId") String camaraId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFinExclusive") LocalDateTime fechaFinExclusive);

    @Query(value = """
            SELECT
                TO_CHAR(CAST(fecha_hora AS DATE), 'YYYY-MM-DD') AS fecha,
                CASE EXTRACT(ISODOW FROM fecha_hora)
                    WHEN 1 THEN 'Lunes'
                    WHEN 2 THEN 'Martes'
                    WHEN 3 THEN 'Miercoles'
                    WHEN 4 THEN 'Jueves'
                    WHEN 5 THEN 'Viernes'
                    WHEN 6 THEN 'Sabado'
                    WHEN 7 THEN 'Domingo'
                END AS "diaSemana",
                CAST(COALESCE(SUM(cantidad), 0) AS INTEGER) AS "totalIngresos"
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
              AND (:camaraId IS NULL OR camara_id = :camaraId)
              AND fecha_hora >= CAST(:fechaInicio AS TIMESTAMP)
              AND fecha_hora < CAST(:fechaFinExclusive AS TIMESTAMP)
            GROUP BY CAST(fecha_hora AS DATE), EXTRACT(ISODOW FROM fecha_hora)
            ORDER BY CAST(fecha_hora AS DATE)
            """, nativeQuery = true)
    List<TraficoDiarioProjection> obtenerTraficoDiario(
            @Param("camaraId") String camaraId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFinExclusive") LocalDateTime fechaFinExclusive);

    @Query(value = """
            SELECT
                CAST(EXTRACT(HOUR FROM fecha_hora) AS INTEGER) AS hora,
                CAST(COALESCE(SUM(cantidad), 0) AS INTEGER) AS "totalIngresos"
            FROM registros_afluencias
            WHERE LOWER(tipo_movimiento) = 'ingreso'
              AND camara_id = :camaraId
              AND fecha_hora >= :inicioDia
              AND fecha_hora < :finDiaExclusive
            GROUP BY EXTRACT(HOUR FROM fecha_hora)
            ORDER BY EXTRACT(HOUR FROM fecha_hora)
            """, nativeQuery = true)
    List<HorasPicoProjection> obtenerHorasPico(
            @Param("camaraId") String camaraId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDiaExclusive") LocalDateTime finDiaExclusive);
}
