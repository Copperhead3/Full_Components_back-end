package com.microservicio.producto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Garantia {

    @NotNull(message = "El período de garantía es obligatorio")
    @Min(
        value = 0,
        message = "El período de garantía no puede ser negativo"
    )
    @Column(name = "garantia_meses")
    private Integer periodoMeses;

    @NotBlank(message = "Las condiciones de garantía son obligatorias")
    @Size(
        max = 1500,
        message = "Las condiciones no pueden superar los 1500 caracteres"
    )
    @Column(name = "garantia_condiciones", length = 1500)
    private String condicionesGarantia;
}