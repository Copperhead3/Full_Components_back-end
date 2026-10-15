package com.microservicio.pago.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReembolsoRequest(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        @Digits(integer = 17, fraction = 2, message = "El monto admite hasta 2 decimales")
        BigDecimal monto,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 255, message = "El motivo admite hasta 255 caracteres")
        String motivo) {
}
