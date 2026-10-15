package com.microservicio.pago.exception;

/** Se lanza cuando una operacion viola una regla de negocio (estado invalido, monto excedido, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
