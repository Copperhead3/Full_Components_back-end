package com.microservicio.inventario.controller;

import java.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservicio.inventario.model.EstadoReserva;
import com.microservicio.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/inventario")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
@Tag(name = "Inventario", description = "Disponibilidad, reservas y trazabilidad")
public class InventarioController {
    @Autowired private InventarioService service;

    // Request bodies kept here so the model layer contains only persistence entities.
    public record RegistroRequest(
        @NotNull @Positive Long idProducto,
        @NotNull @PositiveOrZero Integer cantidadFisica,
        @NotNull @PositiveOrZero Integer stockMinimo) {}

    public record EntradaRequest(
        @NotNull @Positive Integer cantidad,
        @NotBlank @Size(max = 100) String referenciaOperacion) {}

    public record AjusteRequest(
        @NotNull @PositiveOrZero Integer cantidadFisica,
        @NotBlank @Size(max = 100) String referenciaOperacion) {}

    public record MinimoRequest(@NotNull @PositiveOrZero Integer stockMinimo) {}

    public record ReservaRequest(
        @NotNull @Positive Long idProducto,
        @NotNull @Positive Integer cantidadReserva,
        @NotBlank @Size(max = 100) String referenciaCompra) {}

    @PostMapping("/stock")
    @Operation(summary = "Registrar stock de un producto existente")
    public ResponseEntity<Map<String,Object>> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.registrar(datos.idProducto(),datos.cantidadFisica(),datos.stockMinimo()));
    }
    @GetMapping("/stock")
    @Operation(summary = "Listar el stock registrado")
    public List<Map<String,Object>> listar() { return service.listar(); }

    @GetMapping("/productos/{idProducto}")
    @Operation(summary = "Consultar stock disponible por ID del producto")
    public Map<String,Object> consultar(@PathVariable("idProducto") Long idProducto) {
        return service.consultar(idProducto);
    }
    @GetMapping("/productos/{idProducto}/disponibilidad")
    @Operation(summary = "Comprobar una cantidad sin reservarla")
    public Map<String,Object> validar(@PathVariable("idProducto") Long idProducto,
            @RequestParam("cantidad") Integer cantidad) {
        return service.validarCantidad(idProducto,cantidad);
    }
    @PostMapping("/productos/{idProducto}/entradas")
    @Operation(summary = "Agregar unidades físicas con referencia idempotente")
    public Map<String,Object> entrada(@PathVariable("idProducto") Long idProducto,
            @Valid @RequestBody EntradaRequest datos) {
        return service.ajustar(idProducto,datos.cantidad(),datos.referenciaOperacion(),true);
    }
    @PutMapping("/productos/{idProducto}/ajuste")
    @Operation(summary = "Establecer cantidad física, respetando las reservas")
    public Map<String,Object> ajuste(@PathVariable("idProducto") Long idProducto,
            @Valid @RequestBody AjusteRequest datos) {
        return service.ajustar(idProducto,datos.cantidadFisica(),datos.referenciaOperacion(),false);
    }
    @PutMapping("/productos/{idProducto}/minimo")
    @Operation(summary = "Cambiar el umbral de bajo stock")
    public Map<String,Object> minimo(@PathVariable("idProducto") Long idProducto,
            @Valid @RequestBody MinimoRequest datos) {
        return service.cambiarMinimo(idProducto,datos.stockMinimo());
    }
    @PostMapping("/reservas")
    @Operation(summary = "Reservar unidades para una compra")
    public Map<String,Object> reservar(@Valid @RequestBody ReservaRequest datos) {
        return service.reservar(datos.idProducto(),datos.cantidadReserva(),datos.referenciaCompra());
    }
    @GetMapping("/reservas/{idReserva}")
    @Operation(summary = "Consultar el estado vigente de una reserva")
    public Map<String,Object> reserva(@PathVariable("idReserva") Long idReserva) {
        return service.consultarReserva(idReserva);
    }
    @PostMapping("/reservas/{idReserva}/confirmar")
    @Operation(summary = "Confirmar venta y descontar las unidades físicas una sola vez")
    public ResponseEntity<Map<String,Object>> confirmar(@PathVariable("idReserva") Long idReserva) {
        Map<String,Object> respuesta = service.confirmar(idReserva);
        if (respuesta.get("estadoReserva") == EstadoReserva.EXPIRADA)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        return ResponseEntity.ok(respuesta);
    }
    @PostMapping("/reservas/{idReserva}/liberar")
    @Operation(summary = "Liberar unidades reservadas sin descontar stock físico")
    public Map<String,Object> liberar(@PathVariable("idReserva") Long idReserva) {
        return service.liberar(idReserva);
    }
    @GetMapping("/productos/{idProducto}/movimientos")
    @Operation(summary = "Consultar el historial de movimientos físicos")
    public List<Map<String,Object>> movimientos(@PathVariable("idProducto") Long idProducto) {
        return service.listarMovimientos(idProducto);
    }
    @GetMapping("/alertas")
    @Operation(summary = "Consultar alertas de bajo stock")
    public List<Map<String,Object>> alertas(@RequestParam(name = "soloActivas", defaultValue = "true") boolean soloActivas) {
        return service.listarAlertas(soloActivas);
    }
}
