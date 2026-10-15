package com.microservicio.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.producto.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
