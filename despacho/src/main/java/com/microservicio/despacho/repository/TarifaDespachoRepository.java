package com.microservicio.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.despacho.entity.TarifaDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;

@Repository
public interface TarifaDespachoRepository extends JpaRepository<TarifaDespacho, Long> {

    List<TarifaDespacho> findByZonaIdZona(Long idZona);

    List<TarifaDespacho> findByZonaIdZonaAndModalidad(Long idZona, ModalidadEntrega modalidad);
}
