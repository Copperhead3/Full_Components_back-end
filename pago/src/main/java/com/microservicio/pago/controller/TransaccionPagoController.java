package com.microservicio.pago.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.pago.dto.TransaccionPagoResponse;
import com.microservicio.pago.service.TransaccionPagoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionPagoController {

    private final TransaccionPagoService transaccionService;

    @GetMapping("/{id}")
    public TransaccionPagoResponse obtener(@PathVariable Long id) {
        return transaccionService.obtener(id);
    }

    /** Consulta a la pasarela y sincroniza el estado si la transaccion sigue pendiente. */
    @PostMapping("/{id}/consultar-estado")
    public TransaccionPagoResponse consultarEstado(@PathVariable Long id) {
        return transaccionService.consultarEstado(id);
    }
}
