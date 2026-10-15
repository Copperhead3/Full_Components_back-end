package com.microservicio.pedido.dto;

import com.microservicio.pedido.model.EstadoPedido;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para actualizar el estado de un pedido")
public class ActualizarEstadoPedidoDTO {

    @Schema(
        description = "Nuevo estado del pedido",
        example = "EN_PREPARACION",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "El estado del pedido es obligatorio")
    private EstadoPedido estado;

    public ActualizarEstadoPedidoDTO() {
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}