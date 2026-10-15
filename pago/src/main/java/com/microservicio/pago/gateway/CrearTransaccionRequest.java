package com.microservicio.pago.gateway;

import java.math.BigDecimal;

import com.microservicio.pago.enums.MetodoPago;

public record CrearTransaccionRequest(Long idPago, BigDecimal monto, String moneda, MetodoPago metodo) {
}
