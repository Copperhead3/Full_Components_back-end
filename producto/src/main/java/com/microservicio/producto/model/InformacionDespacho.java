package com.microservicio.producto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class InformacionDespacho {

    @NotBlank(message = "Las condiciones de despacho son obligatorias")
    @Size(
        max = 1500,
        message = "Las condiciones no pueden superar los 1500 caracteres"
    )
    @Column(name = "despacho_condiciones", length = 1500)
    private String condicionesDespacho;

    @Size(
        max = 150,
        message = "El plazo no puede superar los 150 caracteres"
    )
    @Column(name = "despacho_plazo_referencial", length = 150)
    private String plazoReferencial;
}