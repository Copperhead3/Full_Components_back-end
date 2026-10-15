package com.microservicio.pago.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.enums.EstadoReembolso;
import com.microservicio.pago.enums.MetodoPago;
import com.microservicio.pago.exception.ReglaNegocioException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pago")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "id_pedido", nullable = false)
    private Long idPedido;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodo;

    /** Agregacion: las transacciones componen el pago pero se persisten por separado. */
    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    @OrderBy("idTransaccion ASC")
    private List<TransaccionPago> transacciones = new ArrayList<>();

    /** Composicion: los reembolsos no existen sin el pago. */
    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idReembolso ASC")
    private List<Reembolso> reembolsos = new ArrayList<>();

    public Pago(Long idPedido, BigDecimal monto, String moneda, MetodoPago metodo) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.moneda = moneda.toUpperCase();
        this.metodo = metodo;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
    }

    // ------------------------------------------------------------------
    // Comportamiento del diagrama
    // ------------------------------------------------------------------

    /** Valida que el pago pueda enviarse a la pasarela. */
    public void iniciarPago() {
        exigirEstado(EstadoPago.PENDIENTE, "iniciar");
    }

    /** Toma el resultado de la ultima transaccion y lo refleja en el estado del pago. */
    public void confirmarResultado() {
        exigirEstado(EstadoPago.PENDIENTE, "confirmar el resultado de");
        TransaccionPago ultima = transacciones.isEmpty() ? null : transacciones.get(transacciones.size() - 1);
        if (ultima == null) {
            throw new ReglaNegocioException("El pago " + idPago + " no tiene transacciones registradas");
        }
        EstadoPago resultado = ultima.getEstado();
        if (resultado == EstadoPago.APROBADO || resultado == EstadoPago.RECHAZADO) {
            this.estado = resultado;
        }
        // Si la transaccion sigue PENDIENTE, el pago tambien.
    }

    public void anular() {
        exigirEstado(EstadoPago.PENDIENTE, "anular");
        this.estado = EstadoPago.ANULADO;
    }

    public Reembolso solicitarReembolso(BigDecimal montoReembolso) {
        return solicitarReembolso(montoReembolso, "Sin motivo especificado");
    }

    public Reembolso solicitarReembolso(BigDecimal montoReembolso, String motivo) {
        if (estado != EstadoPago.APROBADO && estado != EstadoPago.REEMBOLSO_PARCIAL) {
            throw new ReglaNegocioException(
                    "Solo se puede reembolsar un pago APROBADO o con REEMBOLSO_PARCIAL (estado actual: " + estado + ")");
        }
        if (montoReembolso == null || montoReembolso.signum() <= 0) {
            throw new ReglaNegocioException("El monto del reembolso debe ser mayor a cero");
        }
        BigDecimal disponible = montoDisponibleReembolso();
        if (montoReembolso.compareTo(disponible) > 0) {
            throw new ReglaNegocioException(
                    "El monto del reembolso (" + montoReembolso + ") excede el disponible (" + disponible + ")");
        }
        Reembolso reembolso = new Reembolso(this, montoReembolso, motivo);
        reembolsos.add(reembolso);
        return reembolso;
    }

    // ------------------------------------------------------------------
    // Utilidades de dominio
    // ------------------------------------------------------------------

    public void agregarTransaccion(TransaccionPago transaccion) {
        transaccion.asignarPago(this);
        transacciones.add(transaccion);
    }

    /** Suma de reembolsos ya aprobados. */
    public BigDecimal montoReembolsado() {
        return reembolsos.stream()
                .filter(r -> r.getEstado() == EstadoReembolso.APROBADO)
                .map(Reembolso::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Monto aun reembolsable: descuenta los reembolsos aprobados y los que estan en curso. */
    public BigDecimal montoDisponibleReembolso() {
        BigDecimal comprometido = reembolsos.stream()
                .filter(r -> r.getEstado() != EstadoReembolso.RECHAZADO)
                .map(Reembolso::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return monto.subtract(comprometido);
    }

    /** Invocado por Reembolso cuando es aprobado. */
    void recalcularEstadoPorReembolsos() {
        this.estado = montoReembolsado().compareTo(monto) >= 0
                ? EstadoPago.REEMBOLSADO
                : EstadoPago.REEMBOLSO_PARCIAL;
    }

    private void exigirEstado(EstadoPago esperado, String accion) {
        if (estado != esperado) {
            throw new ReglaNegocioException("No se puede " + accion + " un pago en estado " + estado
                    + " (se requiere " + esperado + ")");
        }
    }
}
