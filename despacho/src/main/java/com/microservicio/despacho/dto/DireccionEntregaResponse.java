package com.microservicio.despacho.dto;

import com.microservicio.despacho.entity.DireccionEntrega;

public record DireccionEntregaResponse(
        String region, String comuna, String calle, String numero, String departamento, String referencia) {

    public static DireccionEntregaResponse from(DireccionEntrega d) {
        if (d == null) {
            return null;
        }
        return new DireccionEntregaResponse(d.getRegion(), d.getComuna(), d.getCalle(), d.getNumero(),
                d.getDepartamento(), d.getReferencia());
    }
}
