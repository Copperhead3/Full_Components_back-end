package com.microservicio.despacho.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.despacho.dto.ActualizarEstadoRequest;
import com.microservicio.despacho.dto.AsignarOperadorRequest;
import com.microservicio.despacho.dto.DespachoRequest;
import com.microservicio.despacho.dto.DespachoResponse;
import com.microservicio.despacho.dto.HistorialEstadoResponse;
import com.microservicio.despacho.dto.SeguimientoResponse;
import com.microservicio.despacho.enums.EstadoDespacho;
import com.microservicio.despacho.service.DespachoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @PostMapping
    public ResponseEntity<DespachoResponse> crear(@Valid @RequestBody DespachoRequest request) {
        DespachoResponse creado = despachoService.crear(request);
        return ResponseEntity.created(URI.create("/api/despachos/" + creado.idDespacho())).body(creado);
    }

    @GetMapping
    public List<DespachoResponse> listar(@RequestParam(required = false) EstadoDespacho estado) {
        return despachoService.listar(estado);
    }

    @GetMapping("/{id}")
    public DespachoResponse obtener(@PathVariable Long id) {
        return despachoService.obtener(id);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialEstadoResponse> historial(@PathVariable Long id) {
        return despachoService.historial(id);
    }

    /** Consulta publica de seguimiento por numero. */
    @GetMapping("/seguimiento/{numeroSeguimiento}")
    public SeguimientoResponse seguimiento(@PathVariable String numeroSeguimiento) {
        return despachoService.seguimiento(numeroSeguimiento);
    }

    @PutMapping("/{id}/operador")
    public DespachoResponse asignarOperador(@PathVariable Long id, @Valid @RequestBody AsignarOperadorRequest request) {
        return despachoService.asignarOperador(id, request.idOperador());
    }

    @PatchMapping("/{id}/estado")
    public DespachoResponse actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoRequest request) {
        return despachoService.actualizarEstado(id, request);
    }
}
