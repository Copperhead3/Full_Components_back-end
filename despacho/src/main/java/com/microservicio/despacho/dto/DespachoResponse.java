package com.microservicio.despacho.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.despacho.entity.Despacho;
import com.microservicio.despacho.enums.EstadoDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;

public record DespachoResponse(
        Long idDespacho,
        Long idPedido,
        LocalDateTime fechaCreacion,
        ModalidadEntrega modalidad,
        EstadoDespacho estado,
        BigDecimal costo,
        LocalDateTime fechaEstimadaEntrega,
        String numeroSeguimiento,
        DireccionEntregaResponse direccion,
        OperadorLogisticoResponse operador) {

    public static DespachoResponse from(Despacho d) {
        return new DespachoResponse(d.getIdDespacho(), d.getIdPedido(), d.getFechaCreacion(), d.getModalidad(), d.getEstado(),
                d.getCosto(), d.getFechaEstimadaEntrega(), d.getNumeroSeguimiento(),
                DireccionEntregaResponse.from(d.getDireccion()),
                d.getOperador() == null ? null : OperadorLogisticoResponse.from(d.getOperador()));
    }
}
