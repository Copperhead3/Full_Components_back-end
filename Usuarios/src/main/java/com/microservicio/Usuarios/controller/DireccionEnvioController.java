package com.microservicio.Usuarios.controller;

import com.microservicio.Usuarios.model.DireccionEnvio;
import com.microservicio.Usuarios.service.DireccionEnvioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{idCliente}/direcciones")
@CrossOrigin(origins = "*")
@Tag(name = "Direcciones de envío", description = "Gestión de las direcciones de envío asociadas a los clientes")
public class DireccionEnvioController {

        private final DireccionEnvioService direccionService;

        public DireccionEnvioController(
                        DireccionEnvioService direccionService) {

                this.direccionService = direccionService;
        }

        // AGREGAR DIRECCIÓN
        @Operation(summary = "Agregar dirección", description = "Agrega una nueva dirección de envío a un cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Dirección agregada correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de la dirección inválidos"),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        })
        @PostMapping
        public ResponseEntity<DireccionEnvio> agregarDireccion(
                        @PathVariable Long idCliente,
                        @Valid @RequestBody DireccionEnvio direccion) {

                DireccionEnvio nuevaDireccion = direccionService.agregarDireccion(idCliente, direccion);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(nuevaDireccion);
        }

        // LISTAR DIRECCIONES DEL CLIENTE
        @Operation(summary = "Listar direcciones", description = "Obtiene todas las direcciones de envío asociadas a un cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Direcciones obtenidas correctamente"),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        })
        @GetMapping
        public ResponseEntity<List<DireccionEnvio>> listarDirecciones(
                        @PathVariable Long idCliente) {

                return ResponseEntity.ok(
                                direccionService.listarDirecciones(idCliente));
        }

        // ACTUALIZAR DIRECCIÓN
        @Operation(summary = "Actualizar dirección", description = "Actualiza los datos de una dirección de envío perteneciente a un cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Dirección actualizada correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de la dirección inválidos"),
                        @ApiResponse(responseCode = "404", description = "Cliente o dirección no encontrada")
        })
        @PutMapping("/{idDireccion}")
        public ResponseEntity<DireccionEnvio> actualizarDireccion(
                        @PathVariable Long idCliente,
                        @PathVariable Long idDireccion,
                        @Valid @RequestBody DireccionEnvio direccion) {

                DireccionEnvio direccionActualizada = direccionService.actualizarDireccion(
                                idCliente,
                                idDireccion,
                                direccion);

                return ResponseEntity.ok(direccionActualizada);
        }

        // ELIMINAR DIRECCIÓN
        @Operation(summary = "Eliminar dirección", description = "Elimina una dirección de envío perteneciente a un cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Dirección eliminada correctamente"),
                        @ApiResponse(responseCode = "404", description = "Cliente o dirección no encontrada")
        })
        @DeleteMapping("/{idDireccion}")
        public ResponseEntity<Void> eliminarDireccion(
                        @PathVariable Long idCliente,
                        @PathVariable Long idDireccion) {

                direccionService.eliminarDireccion(
                                idCliente,
                                idDireccion);

                return ResponseEntity.noContent().build();
        }

        // ESTABLECER COMO PREDETERMINADA
        @Operation(summary = "Establecer dirección predeterminada", description = "Establece una dirección como predeterminada y quita esta condición de la dirección predeterminada anterior")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Dirección establecida como predeterminada"),
                        @ApiResponse(responseCode = "404", description = "Cliente o dirección no encontrada")
        })
        @PutMapping("/{idDireccion}/predeterminada")
        public ResponseEntity<DireccionEnvio> establecerPredeterminada(
                        @PathVariable Long idCliente,
                        @PathVariable Long idDireccion) {

                return ResponseEntity.ok(
                                direccionService.establecerPredeterminada(
                                                idCliente,
                                                idDireccion));
        }
}