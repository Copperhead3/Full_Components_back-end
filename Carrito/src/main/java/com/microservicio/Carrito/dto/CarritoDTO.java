package com.microservicio.Carrito.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarritoDTO {

    private Long idCarrito;
    private Long idCliente;
    private LocalDateTime fechaCreacion;
    private List<ItemCarritoDTO> items = new ArrayList<>();
    private BigDecimal subtotal;

    public CarritoDTO() {
    }

    public CarritoDTO(
            Long idCarrito,
            Long idCliente,
            LocalDateTime fechaCreacion,
            List<ItemCarritoDTO> items,
            BigDecimal subtotal) {

        this.idCarrito = idCarrito;
        this.idCliente = idCliente;
        this.fechaCreacion = fechaCreacion;
        this.items = items;
        this.subtotal = subtotal;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}