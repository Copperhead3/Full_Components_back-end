package com.microservicio.despacho.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.microservicio.despacho.entity.Despacho;
import com.microservicio.despacho.enums.EstadoDespacho;

public record SeguimientoResponse(
        String numeroSeguimiento,
        EstadoDespacho estado,
        LocalDateTime fechaEstimadaEntrega,
        List<HistorialEstadoResponse> historial) {

    public static SeguimientoResponse from(Despacho d) {
        return new SeguimientoResponse(d.getNumeroSeguimiento(), d.getEstado(), d.getFechaEstimadaEntrega(),
                d.getHistorial().stream().map(HistorialEstadoResponse::from).toList());
    }
}
