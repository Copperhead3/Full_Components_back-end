package com.microservicio.pago.gateway;

import com.microservicio.pago.enums.EstadoPago;

public record ResultadoPago(String codigoProveedor, EstadoPago estado, String respuesta) {
}
