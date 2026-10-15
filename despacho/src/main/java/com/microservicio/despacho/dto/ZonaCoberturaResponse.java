package com.microservicio.despacho.dto;

import com.microservicio.despacho.entity.ZonaCobertura;

public record ZonaCoberturaResponse(Long idZona, String region, String comuna, boolean activa) {

    public static ZonaCoberturaResponse from(ZonaCobertura z) {
        return new ZonaCoberturaResponse(z.getIdZona(), z.getRegion(), z.getComuna(), z.isActiva());
    }
}
