package com.microservicio.despacho.dto;

import com.microservicio.despacho.entity.OperadorLogistico;

public record OperadorLogisticoResponse(Long idOperador, String nombre, String contacto, String codigoOperador) {

    public static OperadorLogisticoResponse from(OperadorLogistico o) {
        return new OperadorLogisticoResponse(o.getIdOperador(), o.getNombre(), o.getContacto(), o.getCodigoOperador());
    }
}
