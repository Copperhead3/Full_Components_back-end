package com.microservicio.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservicio.pago.entity.Reembolso;
import com.microservicio.pago.enums.EstadoReembolso;

public record ReembolsoResponse(
        Long idReembolso,
        Long idPago,
        BigDecimal monto,
        String motivo,
        LocalDateTime fechaSolicitud,
        EstadoReembolso estado) {

    public static ReembolsoResponse from(Reembolso r) {
        return new ReembolsoResponse(r.getIdReembolso(), r.getPago().getIdPago(), r.getMonto(), r.getMotivo(),
                r.getFechaSolicitud(), r.getEstado());
    }
}
