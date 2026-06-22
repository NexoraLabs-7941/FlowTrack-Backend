package com.nexoralabs.flowtrack.reports.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_afluencias")
@Getter
@NoArgsConstructor
public class RegistroAfluencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;

    @Column(name = "camara_id", nullable = false, length = 100)
    private String camaraId;

    public RegistroAfluencia(LocalDateTime fechaHora, Integer cantidad, String tipoMovimiento, String camaraId) {
        this.fechaHora = fechaHora;
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.camaraId = camaraId;
    }
}
