package com.microservicio.despacho.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.despacho.dto.OperadorLogisticoRequest;
import com.microservicio.despacho.dto.OperadorLogisticoResponse;
import com.microservicio.despacho.dto.SeguimientoOperadorResponse;
import com.microservicio.despacho.service.OperadorLogisticoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/operadores")
@RequiredArgsConstructor
public class OperadorLogisticoController {

    private final OperadorLogisticoService operadorService;

    @PostMapping
    public ResponseEntity<OperadorLogisticoResponse> crear(@Valid @RequestBody OperadorLogisticoRequest request) {
        OperadorLogisticoResponse creado = operadorService.crear(request);
        return ResponseEntity.created(URI.create("/api/operadores/" + creado.idOperador())).body(creado);
    }

    @GetMapping
    public List<OperadorLogisticoResponse> listar() {
        return operadorService.listar();
    }

    @GetMapping("/{id}")
    public OperadorLogisticoResponse obtener(@PathVariable Long id) {
        return operadorService.obtener(id);
    }

    @GetMapping("/{id}/seguimiento")
    public SeguimientoOperadorResponse seguimiento(@PathVariable Long id) {
        return operadorService.obtenerSeguimiento(id);
    }
}
