package com.microservicio.pago.gateway;

import java.math.BigDecimal;

public record EjecutarReembolsoRequest(String codigoProveedor, BigDecimal monto, String motivo) {
}
