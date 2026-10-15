package com.microservicio.Carrito.controller;

import com.microservicio.Carrito.dto.ActualizarCantidadDTO;
import com.microservicio.Carrito.dto.AgregarItemDTO;
import com.microservicio.Carrito.dto.CarritoDTO;
import com.microservicio.Carrito.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin("*")
@Tag(name = "Carrito", description = "Gestión del carrito de compras de los clientes de FullComponents")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // =========================================================
    // OBTENER CARRITO
    // HU-044
    // =========================================================

    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito de compras asociado a un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(
            @PathVariable Long idCliente) {

        CarritoDTO carrito = carritoService.obtenerCarrito(idCliente);

        return ResponseEntity.ok(carrito);
    }

    // =========================================================
    // AGREGAR PRODUCTO
    // HU-043
    // =========================================================

    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito del cliente. "
            + "Si el producto ya se encuentra en el carrito, "
            + "se incrementa su cantidad.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del producto o cantidad inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    })
    @PostMapping("/cliente/{idCliente}/items")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @PathVariable Long idCliente,
            @Valid @RequestBody AgregarItemDTO datos) {

        CarritoDTO carrito = carritoService.agregarProducto(
                idCliente,
                datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carrito);
    }

    // =========================================================
    // ACTUALIZAR CANTIDAD
    // HU-045
    // =========================================================

    @Operation(summary = "Actualizar cantidad de un producto", description = "Modifica la cantidad de un producto existente "
            + "en el carrito del cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "La cantidad solicitada supera el stock disponible")
    })
    @PutMapping("/cliente/{idCliente}/items/{idProducto}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Long idCliente,
            @PathVariable Long idProducto,
            @Valid @RequestBody ActualizarCantidadDTO datos) {

        CarritoDTO carrito = carritoService.actualizarCantidad(
                idCliente,
                idProducto,
                datos);

        return ResponseEntity.ok(carrito);
    }

    // =========================================================
    // ELIMINAR PRODUCTO
    // HU-046
    // =========================================================

    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto específico del carrito del cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado")
    })
    @DeleteMapping("/cliente/{idCliente}/items/{idProducto}")
    public ResponseEntity<CarritoDTO> eliminarProducto(
            @PathVariable Long idCliente,
            @PathVariable Long idProducto) {

        CarritoDTO carrito = carritoService.eliminarProducto(
                idCliente,
                idProducto);

        return ResponseEntity.ok(carrito);
    }

    // =========================================================
    // VACIAR CARRITO
    // =========================================================

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos existentes en el carrito del cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito vaciado correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @DeleteMapping("/cliente/{idCliente}/items")
    public ResponseEntity<CarritoDTO> vaciarCarrito(
            @PathVariable Long idCliente) {

        CarritoDTO carrito = carritoService.vaciarCarrito(idCliente);

        return ResponseEntity.ok(carrito);
    }

    // =========================================================
    // VALIDAR PRECIOS
    // HU-048
    // =========================================================

    @Operation(summary = "Validar precios del carrito", description = "Comprueba que los precios almacenados en el carrito "
            + "coincidan con los precios vigentes de los productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precios validados correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado")
    })
    @PutMapping("/cliente/{idCliente}/validar-precios")
    public ResponseEntity<CarritoDTO> validarPrecios(
            @PathVariable Long idCliente) {

        CarritoDTO carrito = carritoService.validarPrecios(idCliente);

        return ResponseEntity.ok(carrito);
    }

    // =========================================================
    // VALIDAR PARA CHECKOUT
    // HU-049
    // =========================================================

    @Operation(summary = "Validar carrito para checkout", description = "Comprueba que el carrito cumpla las condiciones "
            + "necesarias para continuar al proceso de compra.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validación realizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @GetMapping("/cliente/{idCliente}/validar-checkout")
    public ResponseEntity<Boolean> validarParaCheckout(
            @PathVariable Long idCliente) {

        boolean valido = carritoService.validarParaCheckout(idCliente);

        return ResponseEntity.ok(valido);
    }
}