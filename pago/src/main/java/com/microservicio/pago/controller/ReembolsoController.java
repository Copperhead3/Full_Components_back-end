package com.microservicio.pago.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.pago.dto.ReembolsoResponse;
import com.microservicio.pago.service.ReembolsoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reembolsos")
@RequiredArgsConstructor
public class ReembolsoController {

    private final ReembolsoService reembolsoService;

    @GetMapping("/{id}")
    public ReembolsoResponse obtener(@PathVariable Long id) {
        return reembolsoService.obtener(id);
    }

    @PostMapping("/{id}/procesar")
    public ReembolsoResponse procesar(@PathVariable Long id) {
        return reembolsoService.procesar(id);
    }
}
