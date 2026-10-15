package com.microservicio.pedido.service;

import com.microservicio.pedido.client.CarritoClient;
import com.microservicio.pedido.client.DespachoClient;
import com.microservicio.pedido.client.InventarioClient;
import com.microservicio.pedido.client.PagoClient;
import com.microservicio.pedido.client.UsuarioClient;

import com.microservicio.pedido.dto.CrearPedidoDTO;
import com.microservicio.pedido.dto.DetallePedidoDTO;
import com.microservicio.pedido.dto.PedidoDTO;

import com.microservicio.pedido.dto.integracion.*;

import com.microservicio.pedido.model.DetallePedido;
import com.microservicio.pedido.model.EstadoPedido;
import com.microservicio.pedido.model.Pedido;

import com.microservicio.pedido.repository.PedidoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final CarritoClient carritoClient;
    private final UsuarioClient usuarioClient;
    private final InventarioClient inventarioClient;
    private final DespachoClient despachoClient;
    private final PagoClient pagoClient;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PedidoService(
            PedidoRepository pedidoRepository,
            CarritoClient carritoClient,
            UsuarioClient usuarioClient,
            InventarioClient inventarioClient,
            DespachoClient despachoClient,
            PagoClient pagoClient) {

        this.pedidoRepository = pedidoRepository;
        this.carritoClient = carritoClient;
        this.usuarioClient = usuarioClient;
        this.inventarioClient = inventarioClient;
        this.despachoClient = despachoClient;
        this.pagoClient = pagoClient;
    }


    // =========================================================
    // CREAR PEDIDO
    // =========================================================

    @Transactional
    public PedidoDTO crearPedido(CrearPedidoDTO datos) {

        Long idCliente = datos.getIdCliente();

        // -----------------------------------------------------
        // 1. VALIDAR CLIENTE
        // -----------------------------------------------------

        ClienteResponseDTO cliente;

        try {

            cliente = usuarioClient.obtenerCliente(idCliente);

        } catch (Exception e) {

            throw new RuntimeException(
                    "No fue posible obtener el cliente: "
                            + e.getMessage()
            );
        }

        if (cliente == null) {

            throw new RuntimeException(
                    "El cliente no existe"
            );
        }


        // -----------------------------------------------------
        // 2. VALIDAR CARRITO PARA CHECKOUT
        // -----------------------------------------------------

        Boolean carritoValido;

        try {

            carritoValido =
                    carritoClient.validarCheckout(idCliente);

        } catch (Exception e) {

            throw new RuntimeException(
                    "No fue posible validar el carrito: "
                            + e.getMessage()
            );
        }

        if (!Boolean.TRUE.equals(carritoValido)) {

            throw new RuntimeException(
                    "El carrito no cumple las condiciones "
                            + "para realizar la compra"
            );
        }


        // -----------------------------------------------------
        // 3. VALIDAR PRECIOS
        // -----------------------------------------------------

        CarritoResponseDTO carrito;

        try {

            carrito =
                    carritoClient.validarPrecios(idCliente);

        } catch (Exception e) {

            throw new RuntimeException(
                    "No fue posible validar los precios "
                            + "del carrito: "
                            + e.getMessage()
            );
        }

        validarCarrito(carrito);


        // -----------------------------------------------------
        // 4. CREAR PEDIDO PENDIENTE
        // -----------------------------------------------------

        Pedido pedido =
                construirPedido(idCliente, carrito);

        pedido = pedidoRepository.save(pedido);


        // -----------------------------------------------------
        // 5. RESERVAR STOCK
        // -----------------------------------------------------

        try {

            reservarStock(pedido);

        } catch (Exception e) {

            /*
             * Puede haber reservas creadas antes de que
             * falle otro producto.
             *
             * Por eso liberamos todas las reservas que
             * hayan alcanzado a generarse.
             */

            liberarReservas(pedido);

            pedido.cancelar();

            pedidoRepository.save(pedido);

            throw new RuntimeException(
                    "No fue posible reservar el stock "
                            + "del pedido: "
                            + e.getMessage()
            );
        }


        // -----------------------------------------------------
        // 6. CREAR DESPACHO
        // -----------------------------------------------------

        try {

            DespachoResponseDTO despacho =
                    crearDespacho(datos);

            validarRespuestaDespacho(despacho);

            pedido.setIdDespacho(
                    despacho.getIdDespacho()
            );

            pedido.setCostoDespacho(
                    despacho.getCosto() != null
                            ? despacho.getCosto()
                            : BigDecimal.ZERO
            );

            pedido = pedidoRepository.save(pedido);

        } catch (Exception e) {

            liberarReservas(pedido);

            pedido.cancelar();

            pedidoRepository.save(pedido);

            throw new RuntimeException(
                    "No fue posible crear el despacho: "
                            + e.getMessage()
            );
        }


        // -----------------------------------------------------
        // 7. CREAR E INICIAR PAGO
        // -----------------------------------------------------

        try {

            PagoRequestDTO pagoRequest =
                    new PagoRequestDTO(
                            pedido.getTotal(),
                            "CLP",
                            datos.getMetodoPago()
                    );

            PagoResponseDTO pago =
                    pagoClient.crearPago(pagoRequest);

            validarPagoCreado(pago);

            pedido.setIdPago(
                    pago.getIdPago()
            );

            pedidoRepository.save(pedido);


            // -------------------------------------------------
            // INICIAR PAGO
            // -------------------------------------------------

            PagoResponseDTO pagoProcesado =
                    pagoClient.iniciarPago(
                            pago.getIdPago()
                    );

            if (!pagoAprobado(pagoProcesado)) {

                liberarReservas(pedido);

                pedido.cancelar();

                pedidoRepository.save(pedido);

                throw new RuntimeException(
                        "El pago del pedido no fue aprobado"
                );
            }

        } catch (Exception e) {

            /*
             * Si el pago alcanzó a crearse, intentamos
             * anularlo antes de cancelar el pedido.
             */

            intentarAnularPago(pedido);

            liberarReservas(pedido);

            pedido.cancelar();

            pedidoRepository.save(pedido);

            throw new RuntimeException(
                    "No fue posible procesar el pago: "
                            + e.getMessage()
            );
        }


        // -----------------------------------------------------
        // 8. CONFIRMAR RESERVAS DE INVENTARIO
        // -----------------------------------------------------

        try {

            confirmarReservas(pedido);

        } catch (Exception e) {

            /*
             * El pago ya fue aprobado.
             *
             * Intentamos compensar:
             *
             * 1. Anular el pago.
             * 2. Liberar las reservas que sea posible.
             * 3. Cancelar el pedido.
             */

            intentarAnularPago(pedido);

            liberarReservas(pedido);

            pedido.cancelar();

            pedidoRepository.save(pedido);

            throw new RuntimeException(
                    "No fue posible confirmar el inventario. "
                            + "El pedido fue cancelado: "
                            + e.getMessage()
            );
        }


        // -----------------------------------------------------
        // 9. CONFIRMAR PEDIDO
        // -----------------------------------------------------

        pedido.actualizarEstado(
                EstadoPedido.CONFIRMADO
        );

        pedido = pedidoRepository.save(pedido);


        // -----------------------------------------------------
        // 10. VACIAR CARRITO
        // -----------------------------------------------------

        try {

            carritoClient.vaciarCarrito(
                    idCliente
            );

        } catch (Exception e) {

            /*
             * IMPORTANTE:
             *
             * El pago fue aprobado.
             * El inventario fue confirmado.
             * El pedido fue confirmado.
             *
             * Por lo tanto un error al vaciar el carrito
             * NO debe cancelar la compra.
             *
             * Más adelante este error podría registrarse
             * mediante logging/auditoría.
             */
        }


        return convertirAPedidoDTO(pedido);
    }


    // =========================================================
    // OBTENER PEDIDO POR ID
    // =========================================================

    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedido(Long idPedido) {

        Pedido pedido =
                buscarPedido(idPedido);

        return convertirAPedidoDTO(pedido);
    }


    // =========================================================
    // OBTENER PEDIDOS DE UN CLIENTE
    // =========================================================

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosCliente(
            Long idCliente) {

        if (idCliente == null || idCliente <= 0) {

            throw new RuntimeException(
                    "El ID del cliente debe ser mayor a 0"
            );
        }

        List<Pedido> pedidos =
                pedidoRepository.findByIdCliente(
                        idCliente
                );

        return convertirListaAPedidoDTO(
                pedidos
        );
    }


    // =========================================================
    // OBTENER TODOS LOS PEDIDOS
    // =========================================================

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerTodos() {

        List<Pedido> pedidos =
                pedidoRepository.findAll();

        return convertirListaAPedidoDTO(
                pedidos
        );
    }


    // =========================================================
    // OBTENER PEDIDOS POR ESTADO
    // =========================================================

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPorEstado(
            EstadoPedido estado) {

        if (estado == null) {

            throw new RuntimeException(
                    "El estado es obligatorio"
            );
        }

        List<Pedido> pedidos =
                pedidoRepository.findByEstado(
                        estado
                );

        return convertirListaAPedidoDTO(
                pedidos
        );
    }


    // =========================================================
    // ACTUALIZAR ESTADO
    // =========================================================

    @Transactional
    public PedidoDTO actualizarEstado(
            Long idPedido,
            EstadoPedido nuevoEstado) {

        if (nuevoEstado == null) {

            throw new RuntimeException(
                    "El nuevo estado es obligatorio"
            );
        }

        Pedido pedido =
                buscarPedido(idPedido);

        validarTransicionEstado(
                pedido.getEstado(),
                nuevoEstado
        );

        pedido.actualizarEstado(
                nuevoEstado
        );

        pedido =
                pedidoRepository.save(pedido);

        return convertirAPedidoDTO(pedido);
    }


    // =========================================================
    // CANCELAR PEDIDO
    // =========================================================

    @Transactional
    public PedidoDTO cancelarPedido(
            Long idPedido) {

        Pedido pedido =
                buscarPedido(idPedido);

        if (pedido.getEstado()
                == EstadoPedido.CANCELADO) {

            throw new RuntimeException(
                    "El pedido ya se encuentra cancelado"
            );
        }

        if (pedido.getEstado()
                == EstadoPedido.ENTREGADO) {

            throw new RuntimeException(
                    "No se puede cancelar un pedido entregado"
            );
        }

        /*
         * Si existe un pago asociado intentamos anularlo.
         *
         * A diferencia de una compensación interna,
         * aquí sí detenemos la cancelación si Pago rechaza
         * la operación, porque no queremos marcar como
         * cancelado un pedido cuyo cobro continúa vigente.
         */

        if (pedido.getIdPago() != null) {

            try {

                pagoClient.anularPago(
                        pedido.getIdPago()
                );

            } catch (Exception e) {

                throw new RuntimeException(
                        "No fue posible anular el pago "
                                + "del pedido: "
                                + e.getMessage()
                );
            }
        }

        liberarReservas(pedido);

        pedido.cancelar();

        pedido =
                pedidoRepository.save(pedido);

        return convertirAPedidoDTO(pedido);
    }


    // =========================================================
    // CONSTRUIR PEDIDO DESDE CARRITO
    // =========================================================

    private Pedido construirPedido(
            Long idCliente,
            CarritoResponseDTO carrito) {

        Pedido pedido =
                new Pedido();

        pedido.setIdCliente(idCliente);

        pedido.setEstado(
                EstadoPedido.PENDIENTE
        );

        pedido.setCostoDespacho(
                BigDecimal.ZERO
        );


        for (ItemCarritoResponseDTO item :
                carrito.getItems()) {

            validarItemCarrito(item);

            DetallePedido detalle =
                    new DetallePedido();

            detalle.setIdProducto(
                    item.getIdProducto()
            );

            detalle.setNombreProducto(
                    item.getNombreProducto()
            );

            detalle.setCantidad(
                    item.getCantidad()
            );

            detalle.setPrecioUnitario(
                    item.getPrecioUnitario()
            );

            /*
             * No confiamos en el subtotal enviado por
             * Carrito.
             *
             * Pedidos lo vuelve a calcular.
             */

            detalle.setSubtotal(
                    detalle.calcularSubtotal()
            );

            pedido.agregarDetalle(
                    detalle
            );
        }

        return pedido;
    }


    // =========================================================
    // VALIDAR CARRITO
    // =========================================================

    private void validarCarrito(
            CarritoResponseDTO carrito) {

        if (carrito == null) {

            throw new RuntimeException(
                    "No fue posible obtener el carrito"
            );
        }

        if (carrito.getItems() == null
                || carrito.getItems().isEmpty()) {

            throw new RuntimeException(
                    "No se puede crear un pedido "
                            + "con el carrito vacío"
            );
        }
    }


    // =========================================================
    // VALIDAR ITEM DEL CARRITO
    // =========================================================

    private void validarItemCarrito(
            ItemCarritoResponseDTO item) {

        if (item == null) {

            throw new RuntimeException(
                    "El carrito contiene un producto inválido"
            );
        }

        if (item.getIdProducto() == null
                || item.getIdProducto() <= 0) {

            throw new RuntimeException(
                    "El carrito contiene un ID "
                            + "de producto inválido"
            );
        }

        if (item.getNombreProducto() == null
                || item.getNombreProducto()
                        .isBlank()) {

            throw new RuntimeException(
                    "El producto no posee un nombre válido"
            );
        }

        if (item.getCantidad() == null
                || item.getCantidad() < 1) {

            throw new RuntimeException(
                    "La cantidad del producto debe "
                            + "ser mayor a 0"
            );
        }

        if (item.getPrecioUnitario() == null
                || item.getPrecioUnitario()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "El producto posee un precio inválido"
            );
        }
    }


    // =========================================================
    // RESERVAR STOCK
    // =========================================================

    private void reservarStock(
            Pedido pedido) {

        for (DetallePedido detalle :
                pedido.getDetalles()) {

            String referencia =
                    "PEDIDO-"
                            + pedido.getIdPedido()
                            + "-PRODUCTO-"
                            + detalle.getIdProducto();

            ReservaStockRequestDTO request =
                    new ReservaStockRequestDTO(
                            detalle.getIdProducto(),
                            detalle.getCantidad(),
                            referencia
                    );

            ReservaStockResponseDTO reserva =
                    inventarioClient.reservar(
                            request
                    );

            if (reserva == null
                    || reserva.getIdReservaStock()
                            == null) {

                throw new RuntimeException(
                        "Inventario no devolvió una reserva "
                                + "válida para el producto "
                                + detalle.getIdProducto()
                );
            }

            detalle.setIdReservaStock(
                    reserva.getIdReservaStock()
            );

            /*
             * Guardamos después de cada reserva.
             *
             * Así queda registrada inmediatamente la
             * relación entre el detalle y su reserva.
             */

            pedidoRepository.save(pedido);
        }
    }


    // =========================================================
    // CONFIRMAR RESERVAS
    // =========================================================

    private void confirmarReservas(
            Pedido pedido) {

        for (DetallePedido detalle :
                pedido.getDetalles()) {

            if (detalle.getIdReservaStock()
                    == null) {

                throw new RuntimeException(
                        "El producto "
                                + detalle.getIdProducto()
                                + " no posee una reserva de stock"
                );
            }

            inventarioClient.confirmarReserva(
                    detalle.getIdReservaStock()
            );
        }
    }


    // =========================================================
    // LIBERAR RESERVAS
    // =========================================================

    private void liberarReservas(
            Pedido pedido) {

        if (pedido == null
                || pedido.getDetalles() == null) {

            return;
        }

        for (DetallePedido detalle :
                pedido.getDetalles()) {

            if (detalle.getIdReservaStock()
                    == null) {

                continue;
            }

            try {

                inventarioClient.liberarReserva(
                        detalle.getIdReservaStock()
                );

            } catch (Exception e) {

                /*
                 * Si una reserva no puede liberarse,
                 * continuamos con las demás.
                 *
                 * No queremos que una compensación fallida
                 * impida intentar compensar el resto.
                 */
            }
        }
    }


    // =========================================================
    // CREAR DESPACHO
    // =========================================================

    private DespachoResponseDTO crearDespacho(
            CrearPedidoDTO datos) {

        if (datos.getModalidadEntrega()
                == null) {

            throw new RuntimeException(
                    "La modalidad de entrega "
                            + "es obligatoria"
            );
        }

        DireccionDespachoDTO direccion = null;


        // -----------------------------------------------------
        // ENTREGA A DOMICILIO
        // -----------------------------------------------------

        if (datos.getModalidadEntrega()
                == ModalidadEntregaDTO.DOMICILIO) {

            if (datos.getIdDireccion()
                    == null) {

                throw new RuntimeException(
                        "Debe seleccionar una dirección "
                                + "para el despacho a domicilio"
                );
            }

            List<DireccionResponseDTO> direcciones =
                    usuarioClient.obtenerDirecciones(
                            datos.getIdCliente()
                    );

            if (direcciones == null
                    || direcciones.isEmpty()) {

                throw new RuntimeException(
                        "El cliente no posee direcciones "
                                + "de envío registradas"
                );
            }

            DireccionResponseDTO direccionUsuario =
                    direcciones.stream()

                            .filter(d ->
                                    d != null
                                    && d.getIdDireccion()
                                            != null
                                    && d.getIdDireccion()
                                            .equals(
                                                datos.getIdDireccion()
                                            )
                            )

                            .findFirst()

                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "La dirección seleccionada "
                                                    + "no pertenece al cliente"
                                    )
                            );

            direccion =
                    convertirDireccion(
                            direccionUsuario
                    );
        }


        // -----------------------------------------------------
        // RETIRO EN TIENDA
        // -----------------------------------------------------

        if (datos.getModalidadEntrega()
                == ModalidadEntregaDTO.RETIRO_EN_TIENDA) {

            direccion = null;
        }


        DespachoRequestDTO request =
                new DespachoRequestDTO(
                        datos.getModalidadEntrega(),
                        direccion
                );

        return despachoClient.crearDespacho(
                request
        );
    }


    // =========================================================
    // VALIDAR RESPUESTA DESPACHO
    // =========================================================

    private void validarRespuestaDespacho(
            DespachoResponseDTO despacho) {

        if (despacho == null) {

            throw new RuntimeException(
                    "Despacho no devolvió una respuesta"
            );
        }

        if (despacho.getIdDespacho()
                == null) {

            throw new RuntimeException(
                    "Despacho no devolvió un "
                            + "identificador válido"
            );
        }

        if (despacho.getIdDespacho() <= 0) {

            throw new RuntimeException(
                    "El identificador del despacho "
                            + "es inválido"
            );
        }

        if (despacho.getCosto() != null
                && despacho.getCosto()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Despacho devolvió un costo inválido"
            );
        }
    }


    // =========================================================
    // CONVERTIR DIRECCIÓN USUARIOS -> DESPACHO
    // =========================================================

    private DireccionDespachoDTO convertirDireccion(
            DireccionResponseDTO origen) {

        if (origen == null) {

            throw new RuntimeException(
                    "La dirección seleccionada es inválida"
            );
        }

        DireccionDespachoDTO destino =
                new DireccionDespachoDTO();

        destino.setRegion(
                origen.getRegion()
        );

        destino.setComuna(
                origen.getComuna()
        );

        destino.setCalle(
                origen.getCalle()
        );

        destino.setNumero(
                origen.getNumero()
        );

        /*
         * Usuarios usa "deptoCasa".
         * Despacho usa "departamento".
         */

        destino.setDepartamento(
                origen.getDeptoCasa()
        );

        destino.setReferencia(
                origen.getReferencia()
        );

        return destino;
    }


    // =========================================================
    // VALIDAR PAGO CREADO
    // =========================================================

    private void validarPagoCreado(
            PagoResponseDTO pago) {

        if (pago == null) {

            throw new RuntimeException(
                    "El servicio de pago no devolvió "
                            + "una respuesta"
            );
        }

        if (pago.getIdPago() == null
                || pago.getIdPago() <= 0) {

            throw new RuntimeException(
                    "El servicio de pago no devolvió "
                            + "un identificador válido"
            );
        }
    }


    // =========================================================
    // COMPROBAR PAGO APROBADO
    // =========================================================

    private boolean pagoAprobado(
            PagoResponseDTO pago) {

        return pago != null
                && pago.getEstado() != null
                && pago.getEstado()
                        .equalsIgnoreCase(
                                "APROBADO"
                        );
    }


    // =========================================================
    // INTENTAR ANULAR PAGO
    // =========================================================

    private void intentarAnularPago(
            Pedido pedido) {

        if (pedido == null
                || pedido.getIdPago() == null) {

            return;
        }

        try {

            pagoClient.anularPago(
                    pedido.getIdPago()
            );

        } catch (Exception e) {

            /*
             * Método utilizado como compensación.
             *
             * No reemplazamos el error original por
             * el error producido al intentar anular.
             *
             * Más adelante podemos registrar este caso
             * mediante logs/auditoría.
             */
        }
    }


    // =========================================================
    // VALIDAR TRANSICIÓN DE ESTADO
    // =========================================================

    private void validarTransicionEstado(
            EstadoPedido actual,
            EstadoPedido nuevo) {

        if (actual == null
                || nuevo == null) {

            throw new RuntimeException(
                    "Los estados del pedido "
                            + "no pueden ser nulos"
            );
        }

        if (actual == nuevo) {

            throw new RuntimeException(
                    "El pedido ya se encuentra "
                            + "en estado " + nuevo
            );
        }

        boolean valida = switch (actual) {

            case PENDIENTE ->
                    nuevo == EstadoPedido.CONFIRMADO
                    || nuevo == EstadoPedido.CANCELADO;

            case CONFIRMADO ->
                    nuevo == EstadoPedido.EN_PREPARACION
                    || nuevo == EstadoPedido.CANCELADO;

            case EN_PREPARACION ->
                    nuevo == EstadoPedido.ENVIADO
                    || nuevo == EstadoPedido.CANCELADO;

            case ENVIADO ->
                    nuevo == EstadoPedido.ENTREGADO;

            case ENTREGADO, CANCELADO ->
                    false;
        };


        if (!valida) {

            throw new RuntimeException(
                    "No se puede cambiar el estado "
                            + "del pedido de "
                            + actual
                            + " a "
                            + nuevo
            );
        }
    }


    // =========================================================
    // BUSCAR PEDIDO
    // =========================================================

    private Pedido buscarPedido(
            Long idPedido) {

        if (idPedido == null
                || idPedido <= 0) {

            throw new RuntimeException(
                    "El ID del pedido debe ser mayor a 0"
            );
        }

        return pedidoRepository
                .findById(idPedido)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pedido no encontrado"
                        )
                );
    }


    // =========================================================
    // CONVERTIR LISTA A DTO
    // =========================================================

    private List<PedidoDTO> convertirListaAPedidoDTO(
            List<Pedido> pedidos) {

        List<PedidoDTO> respuesta =
                new ArrayList<>();

        if (pedidos == null) {
            return respuesta;
        }

        for (Pedido pedido : pedidos) {

            respuesta.add(
                    convertirAPedidoDTO(pedido)
            );
        }

        return respuesta;
    }


    // =========================================================
    // CONVERTIR PEDIDO A DTO
    // =========================================================

    private PedidoDTO convertirAPedidoDTO(
            Pedido pedido) {

        List<DetallePedidoDTO> detallesDTO =
                new ArrayList<>();

        for (DetallePedido detalle :
                pedido.getDetalles()) {

            DetallePedidoDTO detalleDTO =
                    new DetallePedidoDTO(
                            detalle.getIdDetalle(),
                            detalle.getIdProducto(),
                            detalle.getNombreProducto(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getSubtotal(),
                            detalle.getIdReservaStock()
                    );

            detallesDTO.add(
                    detalleDTO
            );
        }


        return new PedidoDTO(
                pedido.getIdPedido(),
                pedido.getIdCliente(),
                pedido.getFechaPedido(),
                pedido.getEstado(),
                pedido.getSubtotal(),
                pedido.getCostoDespacho(),
                pedido.getTotal(),
                pedido.getIdPago(),
                pedido.getIdDespacho(),
                detallesDTO
        );
    }
}