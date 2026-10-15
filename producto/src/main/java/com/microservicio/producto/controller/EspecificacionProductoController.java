package com.microservicio.producto.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservicio.producto.model.*;
import com.microservicio.producto.service.*;

@RestController
@RequestMapping("/api/v1/especificaciones")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class EspecificacionProductoController {
    @Autowired private EspecificacionProductoService service;

    @GetMapping
    public List<EspecificacionProducto> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public EspecificacionProducto buscar(@PathVariable("id") Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<EspecificacionProducto> crear(@Valid @RequestBody EspecificacionProducto datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public EspecificacionProducto editar(@PathVariable("id") Long id, @Valid @RequestBody EspecificacionProducto datos) {
        return service.editar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
