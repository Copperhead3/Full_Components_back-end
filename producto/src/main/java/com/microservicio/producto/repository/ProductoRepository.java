package com.microservicio.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.producto.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
