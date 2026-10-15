package com.microservicio.despacho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Direccion de entrega, embebida en Despacho (el diagrama no le asigna identificador propio). */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DireccionEntrega {

    @Column(name = "dir_region", length = 100)
    private String region;

    @Column(name = "dir_comuna", length = 100)
    private String comuna;

    @Column(name = "dir_calle", length = 150)
    private String calle;

    @Column(name = "dir_numero", length = 20)
    private String numero;

    @Column(name = "dir_departamento", length = 20)
    private String departamento;

    @Column(name = "dir_referencia", length = 255)
    private String referencia;

    public DireccionEntrega(String region, String comuna, String calle, String numero,
            String departamento, String referencia) {
        this.region = limpiar(region);
        this.comuna = limpiar(comuna);
        this.calle = limpiar(calle);
        this.numero = limpiar(numero);
        this.departamento = limpiar(departamento);
        this.referencia = limpiar(referencia);
    }

    /** Region, comuna, calle y numero son obligatorios; departamento y referencia son opcionales. */
    public boolean estaCompleta() {
        return tieneTexto(region) && tieneTexto(comuna) && tieneTexto(calle) && tieneTexto(numero);
    }

    private static boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
