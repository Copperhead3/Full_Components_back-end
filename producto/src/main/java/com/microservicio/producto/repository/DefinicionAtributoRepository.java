package com.microservicio.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicio.producto.model.DefinicionAtributo;

public interface DefinicionAtributoRepository extends JpaRepository<DefinicionAtributo, Long> {
}
