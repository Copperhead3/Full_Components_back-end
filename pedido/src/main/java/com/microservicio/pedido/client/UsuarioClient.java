package com.microservicio.pedido.client;

import com.microservicio.pedido.dto.integracion.ClienteResponseDTO;
import com.microservicio.pedido.dto.integracion.DireccionResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "usuarios",
    url = "${services.usuarios.url}"
)
public interface UsuarioClient {

    @GetMapping("/api/clientes/{idCliente}")
    ClienteResponseDTO obtenerCliente(
            @PathVariable("idCliente") Long idCliente
    );

    @GetMapping("/api/clientes/{idCliente}/direcciones")
    List<DireccionResponseDTO> obtenerDirecciones(
            @PathVariable("idCliente") Long idCliente
    );
}