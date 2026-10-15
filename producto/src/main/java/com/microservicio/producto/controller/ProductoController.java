package com.microservicio.producto.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservicio.producto.model.*;
import com.microservicio.producto.service.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class ProductoController {
    @Autowired private ProductoService service;

    @GetMapping
    public List<Producto> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Producto buscar(@PathVariable("id") Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public Producto editar(@PathVariable("id") Long id, @Valid @RequestBody Producto datos) {
        return service.editar(id, datos);
    }

    @PatchMapping("/{id}/publicar")
    public Producto publicar(@PathVariable("id") Long id) { return service.publicar(id); }

    @PatchMapping("/{id}/despublicar")
    public Producto despublicar(@PathVariable("id") Long id) { return service.despublicar(id); }

    @GetMapping("/catalogo")
    public List<Producto> catalogo(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "idCategoria", required = false) Long idCategoria,
            @RequestParam(name = "minimo", required = false) BigDecimal minimo,
            @RequestParam(name = "maximo", required = false) BigDecimal maximo,
            @RequestParam(name = "orden", defaultValue = "nombre") String orden,
            @RequestParam Map<String, String> parametros) {
        return service.catalogo(texto, idCategoria, minimo, maximo, orden, parametros);
    }

    @GetMapping("/catalogo/{id}")
    public Map<String,Object> fichaPublica(@PathVariable("id") Long id) {
        return service.buscarPublicadoConStock(id);
    }

    @PutMapping("/{id}/relacionados")
    public Producto actualizarRelacionados(@PathVariable("id") Long id, @RequestBody List<Long> ids) {
        return service.actualizarRelacionados(id, ids);
    }

    @GetMapping("/catalogo/{id}/relacionados")
    public List<Producto> relacionados(@PathVariable("id") Long id) { return service.relacionados(id); }
}
