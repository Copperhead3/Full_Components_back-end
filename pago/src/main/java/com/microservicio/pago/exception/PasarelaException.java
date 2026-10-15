package com.microservicio.pago.exception;

/** Error al comunicarse con la pasarela de pago. */
public class PasarelaException extends RuntimeException {
    public PasarelaException(String mensaje) {
        super(mensaje);
    }

    public PasarelaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
