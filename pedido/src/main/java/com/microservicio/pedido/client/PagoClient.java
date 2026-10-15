package com.microservicio.pedido.client;

import com.microservicio.pedido.dto.integracion.PagoRequestDTO;
import com.microservicio.pedido.dto.integracion.PagoResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pago",
    url = "${services.pago.url}"
)
public interface PagoClient {

    // =========================================================
    // CREAR PAGO
    // =========================================================

    @PostMapping("/api/pagos")
    PagoResponseDTO crearPago(
            @RequestBody PagoRequestDTO request
    );


    // =========================================================
    // OBTENER PAGO POR ID
    // =========================================================

    @GetMapping("/api/pagos/{idPago}")
    PagoResponseDTO obtenerPago(
            @PathVariable("idPago") Long idPago
    );


    // =========================================================
    // INICIAR / PROCESAR PAGO
    // =========================================================

    @PostMapping("/api/pagos/{idPago}/iniciar")
    PagoResponseDTO iniciarPago(
            @PathVariable("idPago") Long idPago
    );


    // =========================================================
    // ANULAR PAGO
    // =========================================================

    @PostMapping("/api/pagos/{idPago}/anular")
    PagoResponseDTO anularPago(
            @PathVariable("idPago") Long idPago
    );
}