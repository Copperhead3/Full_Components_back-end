package com.microservicio.Usuarios.controller;

import com.microservicio.Usuarios.model.Cliente;
import com.microservicio.Usuarios.service.ClienteService;

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
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Gestión de clientes de FullComponents")
public class ClienteController {

        private final ClienteService clienteService;

        public ClienteController(ClienteService clienteService) {
                this.clienteService = clienteService;
        }

        // REGISTRAR CLIENTE
        @Operation(summary = "Registrar cliente", description = "Registra un nuevo cliente en FullComponents")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Cliente registrado correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos del cliente inválidos"),
                        @ApiResponse(responseCode = "409", description = "Correo o RUT ya registrado")
        })
        @PostMapping
        public ResponseEntity<Cliente> registrarCliente(
                        @Valid @RequestBody Cliente cliente) {

                Cliente nuevoCliente = clienteService.registrarCliente(cliente);

                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        }

        // LISTAR TODOS LOS CLIENTES
        @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes registrados")
        @ApiResponse(responseCode = "200", description = "Clientes obtenidos correctamente")
        @GetMapping
        public ResponseEntity<List<Cliente>> listarClientes() {

                return ResponseEntity.ok(
                                clienteService.listarClientes());
        }

        // BUSCAR CLIENTE POR ID
        @Operation(summary = "Buscar cliente por ID", description = "Obtiene un cliente utilizando su identificador")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Cliente> buscarPorId(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                clienteService.buscarPorId(id));
        }

        // BUSCAR CLIENTE POR RUT
        @Operation(summary = "Buscar cliente por RUT", description = "Obtiene un cliente utilizando su RUT")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        })
        @GetMapping("/rut/{rut}")
        public ResponseEntity<Cliente> buscarPorRut(
                        @PathVariable String rut) {

                return ResponseEntity.ok(
                                clienteService.buscarPorRut(rut));
        }

        // ACTUALIZAR CLIENTE
        @Operation(summary = "Actualizar cliente", description = "Actualiza los datos personales de un cliente existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos del cliente inválidos"),
                        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Cliente> actualizarCliente(
                        @PathVariable Long id,
                        @Valid @RequestBody Cliente cliente) {

                Cliente clienteActualizado = clienteService.actualizarCliente(id, cliente);

                return ResponseEntity.ok(clienteActualizado);
        }
}