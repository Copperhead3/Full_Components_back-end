package com.microservicio.despacho.dto;

import com.microservicio.despacho.entity.DireccionEntrega;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionEntregaRequest(
        @NotBlank(message = "La region es obligatoria") @Size(max = 100) String region,
        @NotBlank(message = "La comuna es obligatoria") @Size(max = 100) String comuna,
        @NotBlank(message = "La calle es obligatoria") @Size(max = 150) String calle,
        @NotBlank(message = "El numero es obligatorio") @Size(max = 20) String numero,
        @Size(max = 20) String departamento,
        @Size(max = 255) String referencia) {

    public DireccionEntrega toEntity() {
        return new DireccionEntrega(region, comuna, calle, numero, departamento, referencia);
    }
}
