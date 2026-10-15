package com.microservicio.despacho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.microservicio.despacho.entity.TarifaDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;

public record TarifaDespachoResponse(
        Long idTarifa,
        Long idZona,
        BigDecimal monto,
        ModalidadEntrega modalidad,
        LocalDate vigenciaDesde,
        LocalDate vigenciaHasta,
        boolean vigenteHoy) {

    public static TarifaDespachoResponse from(TarifaDespacho t) {
        return new TarifaDespachoResponse(t.getIdTarifa(), t.getZona().getIdZona(), t.getMonto(), t.getModalidad(),
                t.getVigenciaDesde(), t.getVigenciaHasta(), t.estaVigente(LocalDate.now()));
    }
}
