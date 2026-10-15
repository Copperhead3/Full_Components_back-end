package com.microservicio.despacho.enums;

public enum EstadoDespacho {
    PENDIENTE,
    PREPARANDO,
    LISTO_PARA_DESPACHO,
    DESPACHADO,
    EN_TRANSITO,
    ENTREGADO,
    INCIDENCIA,
    CANCELADO;

    /** Transiciones validas para despachos a domicilio. */
    public boolean puedeTransicionarA(EstadoDespacho destino) {
        return switch (this) {
            case PENDIENTE -> destino == PREPARANDO || destino == CANCELADO;
            case PREPARANDO -> destino == LISTO_PARA_DESPACHO || destino == CANCELADO;
            case LISTO_PARA_DESPACHO -> destino == DESPACHADO || destino == CANCELADO;
            case DESPACHADO -> destino == EN_TRANSITO || destino == INCIDENCIA;
            case EN_TRANSITO -> destino == ENTREGADO || destino == INCIDENCIA;
            case INCIDENCIA -> destino == EN_TRANSITO || destino == CANCELADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
