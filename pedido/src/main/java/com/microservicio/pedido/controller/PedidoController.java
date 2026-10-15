package com.microservicio.pedido.controller;

import com.microservicio.pedido.dto.ActualizarEstadoPedidoDTO;
import com.microservicio.pedido.dto.CrearPedidoDTO;
import com.microservicio.pedido.dto.PedidoDTO;
import com.microservicio.pedido.model.EstadoPedido;
import com.microservicio.pedido.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin("*")
@Validated
@Tag(
        name = "Pedidos",
        description = "Operaciones para crear, consultar, actualizar y cancelar pedidos"
)
public class PedidoController {

    private final PedidoService pedidoService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    // =========================================================
    // CREAR PEDIDO
    // =========================================================

    @Operation(
            summary = "Crear un pedido",
            description = """
                    Crea y procesa un nuevo pedido a partir del carrito
                    del cliente.

                    El proceso valida el carrito, reserva inventario,
                    crea el despacho, procesa el pago, confirma las
                    reservas de stock y finalmente confirma el pedido.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido creado correctamente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = PedidoDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente, dirección o recurso no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "No fue posible completar el pedido por una regla de negocio"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno durante el procesamiento del pedido"
            )
    })
    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(
            @Valid
            @RequestBody
            CrearPedidoDTO datos) {

        PedidoDTO pedido =
                pedidoService.crearPedido(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);
    }


    // =========================================================
    // OBTENER PEDIDO POR ID
    // =========================================================

    @Operation(
            summary = "Obtener pedido por ID",
            description = "Obtiene toda la información de un pedido específico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado",
                    content = @Content(
                            schema = @Schema(
                                    implementation = PedidoDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de pedido inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            )
    })
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> obtenerPedido(

            @Parameter(
                    description = "Identificador del pedido",
                    example = "1",
                    required = true
            )
            @Positive(
                    message = "El ID del pedido debe ser mayor a 0"
            )
            @PathVariable
            Long idPedido) {

        PedidoDTO pedido =
                pedidoService.obtenerPedido(idPedido);

        return ResponseEntity.ok(pedido);
    }


    // =========================================================
    // OBTENER PEDIDOS DE UN CLIENTE
    // =========================================================

    @Operation(
            summary = "Obtener pedidos de un cliente",
            description = "Obtiene el historial de pedidos realizados por un cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedidos obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de cliente inválido"
            )
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosCliente(

            @Parameter(
                    description = "Identificador del cliente",
                    example = "5",
                    required = true
            )
            @Positive(
                    message = "El ID del cliente debe ser mayor a 0"
            )
            @PathVariable
            Long idCliente) {

        List<PedidoDTO> pedidos =
                pedidoService.obtenerPedidosCliente(
                        idCliente
                );

        return ResponseEntity.ok(pedidos);
    }


    // =========================================================
    // OBTENER TODOS LOS PEDIDOS
    // =========================================================

    @Operation(
            summary = "Obtener todos los pedidos",
            description = """
                    Obtiene todos los pedidos registrados.
                    Este endpoint está pensado principalmente
                    para funciones administrativas.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedidos obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodos() {

        List<PedidoDTO> pedidos =
                pedidoService.obtenerTodos();

        return ResponseEntity.ok(pedidos);
    }


    // =========================================================
    // OBTENER PEDIDOS POR ESTADO
    // =========================================================

    @Operation(
            summary = "Obtener pedidos por estado",
            description = """
                    Obtiene los pedidos que actualmente se encuentran
                    en un estado determinado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedidos obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado de pedido inválido"
            )
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> obtenerPorEstado(

            @Parameter(
                    description = """
                            Estado del pedido.
                            Valores permitidos:
                            PENDIENTE, CONFIRMADO, EN_PREPARACION,
                            ENVIADO, ENTREGADO, CANCELADO
                            """,
                    example = "CONFIRMADO",
                    required = true
            )
            @PathVariable
            EstadoPedido estado) {

        List<PedidoDTO> pedidos =
                pedidoService.obtenerPorEstado(
                        estado
                );

        return ResponseEntity.ok(pedidos);
    }


    // =========================================================
    // ACTUALIZAR ESTADO
    // =========================================================

    @Operation(
            summary = "Actualizar estado de un pedido",
            description = """
                    Actualiza el estado de un pedido respetando
                    las transiciones permitidas.

                    Flujo normal:

                    PENDIENTE -> CONFIRMADO -> EN_PREPARACION
                    -> ENVIADO -> ENTREGADO
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado correctamente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = PedidoDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Transición de estado no permitida"
            )
    })
    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<PedidoDTO> actualizarEstado(

            @Parameter(
                    description = "Identificador del pedido",
                    example = "1",
                    required = true
            )
            @Positive(
                    message = "El ID del pedido debe ser mayor a 0"
            )
            @PathVariable
            Long idPedido,

            @Valid
            @RequestBody
            ActualizarEstadoPedidoDTO datos) {

        PedidoDTO pedido =
                pedidoService.actualizarEstado(
                        idPedido,
                        datos.getEstado()
                );

        return ResponseEntity.ok(pedido);
    }


    // =========================================================
    // CANCELAR PEDIDO
    // =========================================================

    @Operation(
            summary = "Cancelar un pedido",
            description = """
                    Cancela un pedido existente.

                    Si existe un pago asociado, se intenta anular.
                    También se liberan las reservas de inventario
                    asociadas al pedido.

                    Un pedido entregado no puede ser cancelado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido cancelado correctamente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = PedidoDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de pedido inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El pedido no puede ser cancelado"
            )
    })
    @PutMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(

            @Parameter(
                    description = "Identificador del pedido a cancelar",
                    example = "1",
                    required = true
            )
            @Positive(
                    message = "El ID del pedido debe ser mayor a 0"
            )
            @PathVariable
            Long idPedido) {

        PedidoDTO pedido =
                pedidoService.cancelarPedido(
                        idPedido
                );

        return ResponseEntity.ok(pedido);
    }
}