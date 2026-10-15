package com.microservicio.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.inventario.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
