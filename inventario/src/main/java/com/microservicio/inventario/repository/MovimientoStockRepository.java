package com.microservicio.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.inventario.model.MovimientoStock;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
}
