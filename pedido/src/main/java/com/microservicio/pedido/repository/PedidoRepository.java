package com.microservicio.pedido.repository;

import com.microservicio.pedido.model.EstadoPedido;
import com.microservicio.pedido.model.Pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByIdCliente(Long idCliente);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByIdClienteAndEstado(
            Long idCliente,
            EstadoPedido estado
    );
}