package com.microservicio.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.producto.model.ImagenProducto;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
}
