package com.microservicio.despacho.dto;

import com.microservicio.despacho.enums.ModalidadEntrega;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** La direccion es obligatoria solo para DOMICILIO (se valida en el servicio). */
public record DespachoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        @Positive(message = "El ID del pedido debe ser mayor a 0")
        Long idPedido,
        @NotNull(message = "La modalidad es obligatoria") ModalidadEntrega modalidad,
        @Valid DireccionEntregaRequest direccion) {
}
