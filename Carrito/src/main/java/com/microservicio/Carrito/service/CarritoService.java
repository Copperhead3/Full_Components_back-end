package com.microservicio.Carrito.service;

import com.microservicio.Carrito.dto.ActualizarCantidadDTO;
import com.microservicio.Carrito.dto.AgregarItemDTO;
import com.microservicio.Carrito.dto.CarritoDTO;
import com.microservicio.Carrito.dto.ItemCarritoDTO;
import com.microservicio.Carrito.model.Carrito;
import com.microservicio.Carrito.model.ItemCarrito;
import com.microservicio.Carrito.repository.CarritoRepository;
import com.microservicio.Carrito.repository.ItemCarritoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

        private final CarritoRepository carritoRepository;
        private final ItemCarritoRepository itemCarritoRepository;

        public CarritoService(
                        CarritoRepository carritoRepository,
                        ItemCarritoRepository itemCarritoRepository) {

                this.carritoRepository = carritoRepository;
                this.itemCarritoRepository = itemCarritoRepository;
        }

        // =========================================================
        // CREAR / OBTENER CARRITO
        // =========================================================

        @Transactional
        public Carrito obtenerOCrearCarrito(Long idCliente) {

                Optional<Carrito> carritoExistente = carritoRepository.findByIdCliente(idCliente);

                if (carritoExistente.isPresent()) {
                        return carritoExistente.get();
                }

                Carrito carrito = new Carrito();
                carrito.setIdCliente(idCliente);

                return carritoRepository.save(carrito);
        }

        // =========================================================
        // OBTENER CARRITO
        // =========================================================

        @Transactional(readOnly = true)
        public CarritoDTO obtenerCarrito(Long idCliente) {

                Carrito carrito = carritoRepository.findByIdCliente(idCliente)
                                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // AGREGAR PRODUCTO
        // HU-043
        // =========================================================

        @Transactional
        public CarritoDTO agregarProducto(
                        Long idCliente,
                        AgregarItemDTO datos) {

                Carrito carrito = obtenerOCrearCarrito(idCliente);

                Long idProducto = datos.getIdProducto();
                Integer cantidad = datos.getCantidad();

                if (cantidad == null || cantidad < 1) {
                        throw new RuntimeException(
                                        "La cantidad debe ser mayor o igual a 1");
                }

                /*
                 * PENDIENTE INTEGRACIÓN:
                 *
                 * 1. Consultar Producto Service.
                 * 2. Comprobar que el producto exista.
                 * 3. Obtener precio actual.
                 * 4. Consultar Inventario Service.
                 * 5. Validar stock disponible.
                 */

                Optional<ItemCarrito> itemExistente = itemCarritoRepository
                                .findByCarritoIdCarritoAndIdProducto(
                                                carrito.getIdCarrito(),
                                                idProducto);

                if (itemExistente.isPresent()) {

                        ItemCarrito item = itemExistente.get();

                        int nuevaCantidad = item.getCantidad() + cantidad;

                        /*
                         * PENDIENTE:
                         * Validar nuevaCantidad contra Inventario.
                         */

                        item.actualizarCantidad(nuevaCantidad);

                        itemCarritoRepository.save(item);

                } else {

                        ItemCarrito nuevoItem = new ItemCarrito();

                        nuevoItem.setIdProducto(idProducto);
                        nuevoItem.setCantidad(cantidad);

                        /*
                         * TEMPORAL.
                         *
                         * Cuando conectemos Producto Service,
                         * este valor vendrá del precio real del producto.
                         */
                        nuevoItem.setPrecioUnitario(BigDecimal.ZERO);

                        carrito.agregarItem(nuevoItem);

                        carritoRepository.save(carrito);
                }

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // ACTUALIZAR CANTIDAD
        // HU-045
        // =========================================================

        @Transactional
        public CarritoDTO actualizarCantidad(
                        Long idCliente,
                        Long idProducto,
                        ActualizarCantidadDTO datos) {

                Carrito carrito = buscarCarrito(idCliente);

                Integer nuevaCantidad = datos.getCantidad();

                if (nuevaCantidad == null || nuevaCantidad < 1) {
                        throw new RuntimeException(
                                        "La cantidad mínima es 1");
                }

                ItemCarrito item = itemCarritoRepository
                                .findByCarritoIdCarritoAndIdProducto(
                                                carrito.getIdCarrito(),
                                                idProducto)
                                .orElseThrow(() -> new RuntimeException(
                                                "El producto no se encuentra en el carrito"));

                /*
                 * PENDIENTE:
                 * consultar Inventario Service y comprobar:
                 *
                 * nuevaCantidad <= stockDisponible
                 */

                item.actualizarCantidad(nuevaCantidad);

                itemCarritoRepository.save(item);

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // ELIMINAR PRODUCTO
        // HU-046
        // =========================================================

        @Transactional
        public CarritoDTO eliminarProducto(
                        Long idCliente,
                        Long idProducto) {

                Carrito carrito = buscarCarrito(idCliente);

                ItemCarrito item = itemCarritoRepository
                                .findByCarritoIdCarritoAndIdProducto(
                                                carrito.getIdCarrito(),
                                                idProducto)
                                .orElseThrow(() -> new RuntimeException(
                                                "El producto no se encuentra en el carrito"));

                carrito.eliminarItem(item);

                carritoRepository.save(carrito);

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // VACIAR CARRITO
        // =========================================================

        @Transactional
        public CarritoDTO vaciarCarrito(Long idCliente) {

                Carrito carrito = buscarCarrito(idCliente);

                carrito.vaciar();

                carritoRepository.save(carrito);

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // VALIDAR PRECIOS
        // HU-048
        // =========================================================

        @Transactional
        public CarritoDTO validarPrecios(Long idCliente) {

                Carrito carrito = buscarCarrito(idCliente);

                /*
                 * PENDIENTE INTEGRACIÓN CON PRODUCTO SERVICE
                 *
                 * Para cada item:
                 *
                 * 1. Consultar precio actual.
                 * 2. Compararlo con precioUnitario.
                 * 3. Detectar cambios.
                 * 4. Actualizar precio si corresponde.
                 */

                return convertirACarritoDTO(carrito);
        }

        // =========================================================
        // VALIDAR PARA CHECKOUT
        // HU-049
        // =========================================================

        @Transactional(readOnly = true)
        public boolean validarParaCheckout(Long idCliente) {

                Carrito carrito = buscarCarrito(idCliente);

                if (carrito.estaVacio()) {
                        return false;
                }

                /*
                 * PENDIENTE:
                 *
                 * - validar precios con Producto Service.
                 * - validar stock con Inventario Service.
                 */

                return true;
        }

        // =========================================================
        // MÉTODOS PRIVADOS
        // =========================================================

        private Carrito buscarCarrito(Long idCliente) {

                return carritoRepository.findByIdCliente(idCliente)
                                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        }

        private CarritoDTO convertirACarritoDTO(Carrito carrito) {

                List<ItemCarritoDTO> itemsDTO = new ArrayList<>();

                for (ItemCarrito item : carrito.getItems()) {

                        ItemCarritoDTO itemDTO = new ItemCarritoDTO(
                                        item.getIdItem(),
                                        item.getIdProducto(),

                                        // Se obtendrá desde Producto Service.
                                        null,

                                        item.getCantidad(),
                                        item.getPrecioUnitario(),
                                        item.calcularSubtotal());

                        itemsDTO.add(itemDTO);
                }

                return new CarritoDTO(
                                carrito.getIdCarrito(),
                                carrito.getIdCliente(),
                                carrito.getFechaCreacion(),
                                itemsDTO,
                                carrito.calcularSubtotal());
        }
}