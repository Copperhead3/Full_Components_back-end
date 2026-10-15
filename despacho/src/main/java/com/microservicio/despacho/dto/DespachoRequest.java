package com.microservicio.despacho.dto;

import com.microservicio.despacho.enums.ModalidadEntrega;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** La direccion es obligatoria solo para DOMICILIO (se valida en el servicio). */
public record DespachoRequest(
        @NotNull(message = "La modalidad es obligatoria") ModalidadEntrega modalidad,
        @Valid DireccionEntregaRequest direccion) {
}
