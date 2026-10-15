package com.microservicio.despacho.dto;

import com.microservicio.despacho.enums.EstadoDespacho;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarEstadoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoDespacho estado,
        @Size(max = 255, message = "La observacion admite hasta 255 caracteres") String observacion) {
}
