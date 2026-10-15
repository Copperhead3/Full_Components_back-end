package com.microservicio.pago.controller;

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

import com.microservicio.pago.dto.PagoRequest;
import com.microservicio.pago.dto.PagoResponse;
import com.microservicio.pago.dto.ReembolsoRequest;
import com.microservicio.pago.dto.ReembolsoResponse;
import com.microservicio.pago.dto.TransaccionPagoResponse;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.service.PagoService;
import com.microservicio.pago.service.ReembolsoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final ReembolsoService reembolsoService;

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request) {
        PagoResponse creado = pagoService.crear(request);
        return ResponseEntity.created(URI.create("/api/pagos/" + creado.idPago())).body(creado);
    }

    @GetMapping
    public List<PagoResponse> listar(@RequestParam(required = false) EstadoPago estado) {
        return pagoService.listar(estado);
    }

    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Long id) {
        return pagoService.obtener(id);
    }

    @PostMapping("/{id}/iniciar")
    public PagoResponse iniciar(@PathVariable Long id) {
        return pagoService.iniciar(id);
    }

    @PostMapping("/{id}/anular")
    public PagoResponse anular(@PathVariable Long id) {
        return pagoService.anular(id);
    }

    @GetMapping("/{id}/transacciones")
    public List<TransaccionPagoResponse> transacciones(@PathVariable Long id) {
        return pagoService.listarTransacciones(id);
    }

    @PostMapping("/{id}/reembolsos")
    public ResponseEntity<ReembolsoResponse> solicitarReembolso(@PathVariable Long id,
            @Valid @RequestBody ReembolsoRequest request) {
        ReembolsoResponse creado = reembolsoService.solicitar(id, request);
        return ResponseEntity.created(URI.create("/api/reembolsos/" + creado.idReembolso())).body(creado);
    }

    @GetMapping("/{id}/reembolsos")
    public List<ReembolsoResponse> reembolsos(@PathVariable Long id) {
        return reembolsoService.listarPorPago(id);
    }
}
