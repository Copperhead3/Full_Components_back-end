package com.microservicio.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.pago.entity.TransaccionPago;
import com.microservicio.pago.enums.EstadoPago;

public record TransaccionPagoResponse(
        Long idTransaccion,
        Long idPago,
        String codigoProveedor,
        BigDecimal monto,
        LocalDateTime fecha,
        EstadoPago estado,
        String respuestaProveedor) {

    public static TransaccionPagoResponse from(TransaccionPago t) {
        return new TransaccionPagoResponse(t.getIdTransaccion(), t.getPago().getIdPago(), t.getCodigoProveedor(),
                t.getMonto(), t.getFecha(), t.getEstado(), t.getRespuestaProveedor());
    }
}
