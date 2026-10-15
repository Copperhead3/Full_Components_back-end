package com.microservicio.despacho.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.despacho.dto.TarifaDespachoRequest;
import com.microservicio.despacho.dto.TarifaDespachoResponse;
import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.service.TarifaDespachoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaDespachoController {

    private final TarifaDespachoService tarifaService;

    @PostMapping
    public ResponseEntity<TarifaDespachoResponse> crear(@Valid @RequestBody TarifaDespachoRequest request) {
        TarifaDespachoResponse creada = tarifaService.crear(request);
        return ResponseEntity.created(URI.create("/api/tarifas/" + creada.idTarifa())).body(creada);
    }

    @GetMapping
    public List<TarifaDespachoResponse> listar(@RequestParam(required = false) Long idZona) {
        return tarifaService.listar(idZona);
    }

    @GetMapping("/vigentes")
    public List<TarifaDespachoResponse> vigentes(@RequestParam Long idZona, @RequestParam ModalidadEntrega modalidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return tarifaService.vigentes(idZona, modalidad, fecha);
    }

    @GetMapping("/{id}")
    public TarifaDespachoResponse obtener(@PathVariable Long id) {
        return tarifaService.obtener(id);
    }
}
