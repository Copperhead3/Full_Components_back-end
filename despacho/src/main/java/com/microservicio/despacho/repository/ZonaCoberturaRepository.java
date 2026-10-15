package com.microservicio.despacho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.despacho.entity.ZonaCobertura;

@Repository
public interface ZonaCoberturaRepository extends JpaRepository<ZonaCobertura, Long> {

    Optional<ZonaCobertura> findByRegionIgnoreCaseAndComunaIgnoreCase(String region, String comuna);

    List<ZonaCobertura> findByActiva(boolean activa);
}
