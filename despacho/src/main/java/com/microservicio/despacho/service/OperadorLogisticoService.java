package com.microservicio.despacho.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.despacho.dto.OperadorLogisticoRequest;
import com.microservicio.despacho.dto.OperadorLogisticoResponse;
import com.microservicio.despacho.dto.SeguimientoOperadorResponse;
import com.microservicio.despacho.entity.OperadorLogistico;
import com.microservicio.despacho.exception.RecursoNoEncontradoException;
import com.microservicio.despacho.exception.ReglaNegocioException;
import com.microservicio.despacho.repository.OperadorLogisticoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperadorLogisticoService {

    private final OperadorLogisticoRepository operadorRepository;

    @Transactional
    public OperadorLogisticoResponse crear(OperadorLogisticoRequest request) {
        String codigo = request.codigoOperador().trim().toUpperCase();
        if (operadorRepository.existsByCodigoOperador(codigo)) {
            throw new ReglaNegocioException("Ya existe un operador con codigo " + codigo);
        }
        OperadorLogistico operador = new OperadorLogistico(request.nombre(), request.contacto(), codigo);
        return OperadorLogisticoResponse.from(operadorRepository.save(operador));
    }

    @Transactional(readOnly = true)
    public List<OperadorLogisticoResponse> listar() {
        return operadorRepository.findAll().stream().map(OperadorLogisticoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OperadorLogisticoResponse obtener(Long idOperador) {
        return OperadorLogisticoResponse.from(buscar(idOperador));
    }

    @Transactional(readOnly = true)
    public SeguimientoOperadorResponse obtenerSeguimiento(Long idOperador) {
        return new SeguimientoOperadorResponse(idOperador, buscar(idOperador).obtenerSeguimiento());
    }

    private OperadorLogistico buscar(Long idOperador) {
        return operadorRepository.findById(idOperador)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el operador con id " + idOperador));
    }
}
