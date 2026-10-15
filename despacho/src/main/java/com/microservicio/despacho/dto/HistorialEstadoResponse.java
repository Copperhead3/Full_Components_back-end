package com.microservicio.despacho.dto;

import java.time.LocalDateTime;

import com.microservicio.despacho.entity.HistorialEstadoDespacho;
import com.microservicio.despacho.enums.EstadoDespacho;

public record HistorialEstadoResponse(
        Long idHistorial, EstadoDespacho estado, LocalDateTime fecha, String observacion) {

    public static HistorialEstadoResponse from(HistorialEstadoDespacho h) {
        return new HistorialEstadoResponse(h.getIdHistorial(), h.getEstado(), h.getFecha(), h.getObservacion());
    }
}
