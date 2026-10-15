package com.microservicio.Carrito.repository;

import com.microservicio.Carrito.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarritoIdCarrito(Long idCarrito);

    Optional<ItemCarrito> findByCarritoIdCarritoAndIdProducto(
            Long idCarrito,
            Long idProducto
    );
}