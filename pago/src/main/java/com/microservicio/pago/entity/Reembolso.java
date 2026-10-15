package com.microservicio.pago.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.pago.enums.EstadoReembolso;
import com.microservicio.pago.exception.ReglaNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reembolso")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reembolso")
    private Long idReembolso;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReembolso estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    /** Solo se crea a traves de Pago.solicitarReembolso(...) (composicion). */
    Reembolso(Pago pago, BigDecimal monto, String motivo) {
        this.pago = pago;
        this.monto = monto;
        this.motivo = motivo;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = EstadoReembolso.SOLICITADO;
    }

    /** SOLICITADO -> EN_PROCESO. */
    public void procesar() {
        if (estado != EstadoReembolso.SOLICITADO) {
            throw new ReglaNegocioException("Solo se puede procesar un reembolso SOLICITADO (estado actual: " + estado + ")");
        }
        this.estado = EstadoReembolso.EN_PROCESO;
    }

    /**
     * Transiciones validas: SOLICITADO -> EN_PROCESO | RECHAZADO ; EN_PROCESO -> APROBADO | RECHAZADO.
     * Al aprobarse, el pago actualiza su estado (REEMBOLSO_PARCIAL / REEMBOLSADO).
     */
    public void actualizarEstado(EstadoReembolso nuevoEstado) {
        boolean valida = switch (estado) {
            case SOLICITADO -> nuevoEstado == EstadoReembolso.EN_PROCESO || nuevoEstado == EstadoReembolso.RECHAZADO;
            case EN_PROCESO -> nuevoEstado == EstadoReembolso.APROBADO || nuevoEstado == EstadoReembolso.RECHAZADO;
            case APROBADO, RECHAZADO -> false;
        };
        if (!valida) {
            throw new ReglaNegocioException("Transicion de reembolso no permitida: " + estado + " -> " + nuevoEstado);
        }
        this.estado = nuevoEstado;
        if (nuevoEstado == EstadoReembolso.APROBADO) {
            pago.recalcularEstadoPorReembolsos();
        }
    }
}
