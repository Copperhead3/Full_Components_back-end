package com.microservicio.Carrito.repository;

import com.microservicio.Carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository 
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByIdCliente(Long idCliente);

    boolean existsByIdCliente(Long idCliente);
}