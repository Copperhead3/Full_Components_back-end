package com.microservicio.despacho.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarOperadorRequest(@NotNull(message = "El idOperador es obligatorio") Long idOperador) {
}
