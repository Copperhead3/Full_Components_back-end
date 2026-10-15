package com.microservicio.pedido.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @NotNull(message = "El producto es obligatorio")
    @Positive(message = "El ID del producto debe ser mayor a 0")
    @Column(nullable = false)
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El precio unitario no puede ser negativo"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "El subtotal no puede ser negativo"
    )
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Positive(message = "El ID de la reserva debe ser mayor a 0")
    @Column(name = "id_reserva_stock")
    private Long idReservaStock;

    @JsonIgnore
    @NotNull(message = "El pedido asociado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DetallePedido() {
    }


    // =========================================================
    // LÓGICA DE DOMINIO
    // =========================================================

    public BigDecimal calcularSubtotal() {

        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }

        return precioUnitario.multiply(
                BigDecimal.valueOf(cantidad)
        );
    }


    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Long getIdReservaStock() {
        return idReservaStock;
    }

    public void setIdReservaStock(Long idReservaStock) {
        this.idReservaStock = idReservaStock;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}