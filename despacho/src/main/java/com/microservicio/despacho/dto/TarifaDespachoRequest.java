package com.microservicio.despacho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.microservicio.despacho.enums.ModalidadEntrega;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TarifaDespachoRequest(
        @NotNull(message = "La zona es obligatoria") Long idZona,

        @NotNull(message = "El monto es obligatorio")
        @PositiveOrZero(message = "El monto no puede ser negativo")
        @Digits(integer = 17, fraction = 2, message = "El monto admite hasta 2 decimales")
        BigDecimal monto,

        @NotNull(message = "La modalidad es obligatoria") ModalidadEntrega modalidad,

        @NotNull(message = "vigenciaDesde es obligatoria") LocalDate vigenciaDesde,

        /** Opcional: sin fecha, la tarifa queda vigente indefinidamente. */
        LocalDate vigenciaHasta) {
}
