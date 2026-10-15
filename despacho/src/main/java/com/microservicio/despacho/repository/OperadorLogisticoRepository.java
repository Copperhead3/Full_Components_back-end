package com.microservicio.despacho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.despacho.entity.OperadorLogistico;

@Repository
public interface OperadorLogisticoRepository extends JpaRepository<OperadorLogistico, Long> {

    boolean existsByCodigoOperador(String codigoOperador);
}
