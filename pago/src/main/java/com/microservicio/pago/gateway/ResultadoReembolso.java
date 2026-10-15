package com.microservicio.pago.gateway;

public record ResultadoReembolso(boolean aprobado, String codigoReembolso, String mensaje) {
}
