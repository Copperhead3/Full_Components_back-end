package com.microservicio.pago.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.pago.enums.EstadoPago;

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
@Table(name = "transaccion_pago")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransaccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Long idTransaccion;

    @Column(name = "codigo_proveedor", length = 100)
    private String codigoProveedor;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Column(name = "respuesta_proveedor", length = 500)
    private String respuestaProveedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    public TransaccionPago(BigDecimal monto) {
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
    }

    void asignarPago(Pago pago) {
        this.pago = pago;
    }

    /** Guarda lo que respondio la pasarela. */
    public void registrarResultado(EstadoPago nuevoEstado, String codigoProveedor, String respuestaProveedor) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado devuelto por la pasarela no puede ser nulo");
        }
        this.estado = nuevoEstado;
        this.codigoProveedor = codigoProveedor;
        this.respuestaProveedor = respuestaProveedor;
    }

    public EstadoPago consultarEstado() {
        return estado;
    }
}
