package com.microservicio.Carrito.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventario-carrito", url = "${services.inventario.url}")
public interface InventarioClient {
    @GetMapping("/api/v1/inventario/productos/{idProducto}/disponibilidad")
    Map<String, Object> validarDisponibilidad(@PathVariable("idProducto") Long idProducto, @RequestParam("cantidad") Integer cantidad);
}
