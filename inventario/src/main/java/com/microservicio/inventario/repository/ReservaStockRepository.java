package com.microservicio.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.inventario.model.ReservaStock;

public interface ReservaStockRepository extends JpaRepository<ReservaStock, Long> {
}
