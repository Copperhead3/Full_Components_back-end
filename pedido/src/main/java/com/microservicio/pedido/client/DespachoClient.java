package com.microservicio.pedido.client;

import com.microservicio.pedido.dto.integracion.DespachoRequestDTO;
import com.microservicio.pedido.dto.integracion.DespachoResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "despacho",
    url = "${services.despacho.url}"
)
public interface DespachoClient {

    @PostMapping("/api/despachos")
    DespachoResponseDTO crearDespacho(
            @RequestBody DespachoRequestDTO request
    );

    @GetMapping("/api/despachos/{idDespacho}")
    DespachoResponseDTO obtenerDespacho(
            @PathVariable("idDespacho") Long idDespacho
    );
}