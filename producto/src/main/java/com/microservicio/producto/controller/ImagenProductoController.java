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
@RequestMapping("/api/v1/imagenes")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class ImagenProductoController {
    @Autowired private ImagenProductoService service;

    @GetMapping
    public List<ImagenProducto> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ImagenProducto buscar(@PathVariable("id") Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<ImagenProducto> crear(@Valid @RequestBody ImagenProducto datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ImagenProducto editar(@PathVariable("id") Long id, @Valid @RequestBody ImagenProducto datos) {
        return service.editar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
