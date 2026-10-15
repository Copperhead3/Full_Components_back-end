package com.microservicio.pedido.client;

import com.microservicio.pedido.dto.integracion.CarritoResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "carrito",
    url = "${services.carrito.url}"
)
public interface CarritoClient {

    @GetMapping("/api/carritos/cliente/{idCliente}")
    CarritoResponseDTO obtenerCarrito(
            @PathVariable("idCliente") Long idCliente
    );

    @PutMapping("/api/carritos/cliente/{idCliente}/validar-precios")
    CarritoResponseDTO validarPrecios(
            @PathVariable("idCliente") Long idCliente
    );

    @GetMapping("/api/carritos/cliente/{idCliente}/validar-checkout")
    Boolean validarCheckout(
            @PathVariable("idCliente") Long idCliente
    );

    @DeleteMapping("/api/carritos/cliente/{idCliente}/items")
    CarritoResponseDTO vaciarCarrito(
            @PathVariable("idCliente") Long idCliente
    );
}