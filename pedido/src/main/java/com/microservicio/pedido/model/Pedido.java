package com.microservicio.pedido.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser mayor a 0")
    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @NotNull(message = "El estado del pedido es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El subtotal no puede ser negativo"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "El costo de despacho es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El costo de despacho no puede ser negativo"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoDespacho = BigDecimal.ZERO;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El total no puede ser negativo"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Positive(message = "El ID del pago debe ser mayor a 0")
    @Column(name = "id_pago")
    private Long idPago;

    @Positive(message = "El ID del despacho debe ser mayor a 0")
    @Column(name = "id_despacho")
    private Long idDespacho;

    @Valid
    @Size(
        min = 1,
        message = "El pedido debe contener al menos un producto"
    )
    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetallePedido> detalles = new ArrayList<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Pedido() {
    }


    // =========================================================
    // CICLO DE VIDA JPA
    // =========================================================

    @PrePersist
    public void prepararPedido() {

        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }

        actualizarTotales();
    }


    @PreUpdate
    public void actualizarPedido() {
        actualizarTotales();
    }


    // =========================================================
    // LÓGICA DE DOMINIO
    // =========================================================

    public BigDecimal calcularSubtotal() {

        return detalles.stream()
                .map(DetallePedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calcularTotal() {

        BigDecimal subtotalCalculado = calcularSubtotal();

        BigDecimal despacho =
                costoDespacho != null
                        ? costoDespacho
                        : BigDecimal.ZERO;

        return subtotalCalculado.add(despacho);
    }


    private void actualizarTotales() {

        this.subtotal = calcularSubtotal();
        this.total = calcularTotal();

        for (DetallePedido detalle : detalles) {
            detalle.setSubtotal(detalle.calcularSubtotal());
        }
    }


    public void agregarDetalle(DetallePedido detalle) {

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "El detalle no puede ser nulo"
            );
        }

        detalles.add(detalle);
        detalle.setPedido(this);

        actualizarTotales();
    }


    public void eliminarDetalle(DetallePedido detalle) {

        if (detalle == null) {
            return;
        }

        detalles.remove(detalle);
        detalle.setPedido(null);

        actualizarTotales();
    }


    public void actualizarEstado(EstadoPedido nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado del pedido no puede ser nulo"
            );
        }

        this.estado = nuevoEstado;
    }


    public void cancelar() {
        this.estado = EstadoPedido.CANCELADO;
    }


    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getCostoDespacho() {
        return costoDespacho;
    }

    public void setCostoDespacho(BigDecimal costoDespacho) {
        this.costoDespacho = costoDespacho;
        actualizarTotales();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Long getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(Long idDespacho) {
        this.idDespacho = idDespacho;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {

        this.detalles.clear();

        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                agregarDetalle(detalle);
            }
        }

        actualizarTotales();
    }
}