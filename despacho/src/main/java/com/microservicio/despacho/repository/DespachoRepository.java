package com.microservicio.despacho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.despacho.entity.Despacho;
import com.microservicio.despacho.enums.EstadoDespacho;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    List<Despacho> findByEstado(EstadoDespacho estado);

    Optional<Despacho> findByNumeroSeguimiento(String numeroSeguimiento);
}
