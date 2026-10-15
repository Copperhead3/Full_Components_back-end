package com.microservicio.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.inventario.model.AlertaStock;

public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {
}
