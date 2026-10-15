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
@RequestMapping("/api/v1/atributos")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class DefinicionAtributoController {
    @Autowired private DefinicionAtributoService service;

    @GetMapping
    public List<DefinicionAtributo> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public DefinicionAtributo buscar(@PathVariable("id") Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<DefinicionAtributo> crear(@Valid @RequestBody DefinicionAtributo datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public DefinicionAtributo editar(@PathVariable("id") Long id, @Valid @RequestBody DefinicionAtributo datos) {
        return service.editar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
