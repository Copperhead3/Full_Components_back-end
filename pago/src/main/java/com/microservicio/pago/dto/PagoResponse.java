package com.microservicio.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.pago.entity.Pago;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.enums.MetodoPago;

public record PagoResponse(
        Long idPago,
        BigDecimal monto,
        String moneda,
        LocalDateTime fechaCreacion,
        EstadoPago estado,
        MetodoPago metodo,
        BigDecimal montoReembolsado,
        BigDecimal montoDisponibleReembolso) {

    public static PagoResponse from(Pago p) {
        return new PagoResponse(p.getIdPago(), p.getMonto(), p.getMoneda(), p.getFechaCreacion(),
                p.getEstado(), p.getMetodo(), p.montoReembolsado(), p.montoDisponibleReembolso());
    }
}
