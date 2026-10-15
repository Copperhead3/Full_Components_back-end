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
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class CategoriaController {
    @Autowired private CategoriaService service;

    @GetMapping
    public List<Categoria> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Categoria buscar(@PathVariable("id") Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public Categoria editar(@PathVariable("id") Long id, @Valid @RequestBody Categoria datos) {
        return service.editar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
