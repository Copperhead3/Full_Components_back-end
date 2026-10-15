package com.microservicio.despacho.exception;

/** Operacion que viola una regla de negocio (transicion invalida, zona sin cobertura, duplicados, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
