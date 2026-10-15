package com.microservicio.despacho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** "activa" es opcional; por defecto la zona se crea activa. */
public record ZonaCoberturaRequest(
        @NotBlank(message = "La region es obligatoria") @Size(max = 100) String region,
        @NotBlank(message = "La comuna es obligatoria") @Size(max = 100) String comuna,
        Boolean activa) {
}
