package com.microservicio.despacho.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.despacho.dto.TarifaDespachoRequest;
import com.microservicio.despacho.dto.TarifaDespachoResponse;
import com.microservicio.despacho.entity.TarifaDespacho;
import com.microservicio.despacho.entity.ZonaCobertura;
import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.exception.RecursoNoEncontradoException;
import com.microservicio.despacho.repository.TarifaDespachoRepository;
import com.microservicio.despacho.repository.ZonaCoberturaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaDespachoService {

    private final TarifaDespachoRepository tarifaRepository;
    private final ZonaCoberturaRepository zonaRepository;

    @Transactional
    public TarifaDespachoResponse crear(TarifaDespachoRequest request) {
        ZonaCobertura zona = zonaRepository.findById(request.idZona())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la zona con id " + request.idZona()));
        TarifaDespacho tarifa = new TarifaDespacho(zona, request.monto(), request.modalidad(),
                request.vigenciaDesde(), request.vigenciaHasta());
        return TarifaDespachoResponse.from(tarifaRepository.save(tarifa));
    }

    @Transactional(readOnly = true)
    public List<TarifaDespachoResponse> listar(Long idZona) {
        List<TarifaDespacho> tarifas = idZona == null ? tarifaRepository.findAll() : tarifaRepository.findByZonaIdZona(idZona);
        return tarifas.stream().map(TarifaDespachoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TarifaDespachoResponse obtener(Long idTarifa) {
        return tarifaRepository.findById(idTarifa)
                .map(TarifaDespachoResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la tarifa con id " + idTarifa));
    }

    /** Tarifas de la zona y modalidad que estan vigentes en la fecha indicada (hoy por defecto). */
    @Transactional(readOnly = true)
    public List<TarifaDespachoResponse> vigentes(Long idZona, ModalidadEntrega modalidad, LocalDate fecha) {
        if (!zonaRepository.existsById(idZona)) {
            throw new RecursoNoEncontradoException("No existe la zona con id " + idZona);
        }
        LocalDate dia = fecha == null ? LocalDate.now() : fecha;
        return tarifaRepository.findByZonaIdZonaAndModalidad(idZona, modalidad).stream()
                .filter(t -> t.estaVigente(dia))
                .map(TarifaDespachoResponse::from)
                .toList();
    }
}
