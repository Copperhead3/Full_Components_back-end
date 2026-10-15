package com.microservicio.pedido.repository;

import com.microservicio.pedido.model.DetallePedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoIdPedido(Long idPedido);

    Optional<DetallePedido> findByPedidoIdPedidoAndIdProducto(
            Long idPedido,
            Long idProducto
    );
}