package com.microservicio.pago.dto;

import java.math.BigDecimal;

import com.microservicio.pago.enums.MetodoPago;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PagoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        @Positive(message = "El ID del pedido debe ser mayor a 0")
        Long idPedido,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        @Digits(integer = 17, fraction = 2, message = "El monto admite hasta 2 decimales")
        BigDecimal monto,

        @NotBlank(message = "La moneda es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "La moneda debe ser un codigo de 3 letras (ej. CLP, USD)")
        String moneda,

        @NotNull(message = "El metodo de pago es obligatorio")
        MetodoPago metodo) {
}
