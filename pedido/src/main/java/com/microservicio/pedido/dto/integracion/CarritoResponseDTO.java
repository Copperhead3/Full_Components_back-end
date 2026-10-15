
package com.microservicio.pedido.dto.integracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarritoResponseDTO {

    private Long idCarrito;
    private Long idCliente;
    private LocalDateTime fechaCreacion;
    private List<ItemCarritoResponseDTO> items = new ArrayList<>();
    private BigDecimal subtotal;

    public CarritoResponseDTO() {
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

    public List<ItemCarritoResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoResponseDTO> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}