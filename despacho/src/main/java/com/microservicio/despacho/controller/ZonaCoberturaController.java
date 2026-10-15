package com.microservicio.despacho.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.despacho.dto.CoberturaResponse;
import com.microservicio.despacho.dto.ZonaCoberturaRequest;
import com.microservicio.despacho.dto.ZonaCoberturaResponse;
import com.microservicio.despacho.service.ZonaCoberturaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
public class ZonaCoberturaController {

    private final ZonaCoberturaService zonaService;

    @PostMapping
    public ResponseEntity<ZonaCoberturaResponse> crear(@Valid @RequestBody ZonaCoberturaRequest request) {
        ZonaCoberturaResponse creada = zonaService.crear(request);
        return ResponseEntity.created(URI.create("/api/zonas/" + creada.idZona())).body(creada);
    }

    @GetMapping
    public List<ZonaCoberturaResponse> listar(@RequestParam(required = false) Boolean activa) {
        return zonaService.listar(activa);
    }

    @GetMapping("/cubre")
    public CoberturaResponse cubre(@RequestParam String region, @RequestParam String comuna) {
        return zonaService.consultarCobertura(region, comuna);
    }

    @GetMapping("/{id}")
    public ZonaCoberturaResponse obtener(@PathVariable Long id) {
        return zonaService.obtener(id);
    }

    @PostMapping("/{id}/activar")
    public ZonaCoberturaResponse activar(@PathVariable Long id) {
        return zonaService.activar(id);
    }

    @PostMapping("/{id}/desactivar")
    public ZonaCoberturaResponse desactivar(@PathVariable Long id) {
        return zonaService.desactivar(id);
    }
}
