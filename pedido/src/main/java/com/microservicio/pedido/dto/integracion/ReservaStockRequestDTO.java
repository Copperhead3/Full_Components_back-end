package com.microservicio.pedido.dto.integracion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ReservaStockRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    @Positive(message = "El ID del producto debe ser mayor a 0")
    private Long idProducto;

    @NotNull(message = "La cantidad a reservar es obligatoria")
    @Positive(message = "La cantidad a reservar debe ser mayor a 0")
    private Integer cantidadReserva;

    @NotBlank(message = "La referencia de compra es obligatoria")
    @Size(max = 100)
    private String referenciaCompra;

    public ReservaStockRequestDTO() {
    }

    public ReservaStockRequestDTO(
            Long idProducto,
            Integer cantidadReserva,
            String referenciaCompra) {

        this.idProducto = idProducto;
        this.cantidadReserva = cantidadReserva;
        this.referenciaCompra = referenciaCompra;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidadReserva() {
        return cantidadReserva;
    }

    public void setCantidadReserva(Integer cantidadReserva) {
        this.cantidadReserva = cantidadReserva;
    }

    public String getReferenciaCompra() {
        return referenciaCompra;
    }

    public void setReferenciaCompra(String referenciaCompra) {
        this.referenciaCompra = referenciaCompra;
    }
}