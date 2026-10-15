package com.microservicio.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.producto.model.EspecificacionProducto;

public interface EspecificacionProductoRepository extends JpaRepository<EspecificacionProducto, Long> {
}
