package com.microservicio.despacho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OperadorLogisticoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String nombre,
        @NotBlank(message = "El contacto es obligatorio") @Size(max = 150) String contacto,
        @NotBlank(message = "El codigo de operador es obligatorio") @Size(max = 50) String codigoOperador) {
}
