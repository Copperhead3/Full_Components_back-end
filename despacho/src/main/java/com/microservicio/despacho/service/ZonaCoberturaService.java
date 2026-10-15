package com.microservicio.despacho.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.despacho.dto.CoberturaResponse;
import com.microservicio.despacho.dto.ZonaCoberturaRequest;
import com.microservicio.despacho.dto.ZonaCoberturaResponse;
import com.microservicio.despacho.entity.ZonaCobertura;
import com.microservicio.despacho.exception.RecursoNoEncontradoException;
import com.microservicio.despacho.exception.ReglaNegocioException;
import com.microservicio.despacho.repository.ZonaCoberturaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZonaCoberturaService {

    private final ZonaCoberturaRepository zonaRepository;

    @Transactional
    public ZonaCoberturaResponse crear(ZonaCoberturaRequest request) {
        if (zonaRepository.findByRegionIgnoreCaseAndComunaIgnoreCase(request.region().trim(), request.comuna().trim())
                .isPresent()) {
            throw new ReglaNegocioException("Ya existe una zona para " + request.comuna() + ", " + request.region());
        }
        boolean activa = request.activa() == null || request.activa();
        return ZonaCoberturaResponse.from(
                zonaRepository.save(new ZonaCobertura(request.region(), request.comuna(), activa)));
    }

    @Transactional(readOnly = true)
    public List<ZonaCoberturaResponse> listar(Boolean activa) {
        List<ZonaCobertura> zonas = activa == null ? zonaRepository.findAll() : zonaRepository.findByActiva(activa);
        return zonas.stream().map(ZonaCoberturaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ZonaCoberturaResponse obtener(Long idZona) {
        return ZonaCoberturaResponse.from(buscar(idZona));
    }

    @Transactional
    public ZonaCoberturaResponse activar(Long idZona) {
        ZonaCobertura zona = buscar(idZona);
        zona.activar();
        return ZonaCoberturaResponse.from(zona);
    }

    @Transactional
    public ZonaCoberturaResponse desactivar(Long idZona) {
        ZonaCobertura zona = buscar(idZona);
        zona.desactivar();
        return ZonaCoberturaResponse.from(zona);
    }

    @Transactional(readOnly = true)
    public CoberturaResponse consultarCobertura(String region, String comuna) {
        boolean cubierta = zonaRepository.findByRegionIgnoreCaseAndComunaIgnoreCase(region.trim(), comuna.trim())
                .map(ZonaCobertura::isActiva)
                .orElse(false);
        return new CoberturaResponse(region, comuna, cubierta);
    }

    private ZonaCobertura buscar(Long idZona) {
        return zonaRepository.findById(idZona)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la zona con id " + idZona));
    }
}
