package com.microservicio.pedido.client;

import com.microservicio.pedido.dto.integracion.ReservaStockRequestDTO;
import com.microservicio.pedido.dto.integracion.ReservaStockResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "inventario",
    url = "${services.inventario.url}"
)
public interface InventarioClient {

    @PostMapping("/api/v1/inventario/reservas")
    ReservaStockResponseDTO reservar(
            @RequestBody ReservaStockRequestDTO request
    );

    @GetMapping("/api/v1/inventario/reservas/{idReserva}")
    ReservaStockResponseDTO obtenerReserva(
            @PathVariable("idReserva") Long idReserva
    );

    @PostMapping("/api/v1/inventario/reservas/{idReserva}/confirmar")
    ReservaStockResponseDTO confirmarReserva(
            @PathVariable("idReserva") Long idReserva
    );

    @PostMapping("/api/v1/inventario/reservas/{idReserva}/liberar")
    ReservaStockResponseDTO liberarReserva(
            @PathVariable("idReserva") Long idReserva
    );
}