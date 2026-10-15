package com.microservicio.Carrito.client;

import com.microservicio.Carrito.dto.integracion.ProductoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "producto-carrito", url = "${services.producto.url}")
public interface ProductoClient {
    @GetMapping("/api/v1/productos/{id}")
    ProductoResponseDTO obtenerProducto(@PathVariable("id") Long id);
}
