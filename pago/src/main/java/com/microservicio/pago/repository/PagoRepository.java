package com.microservicio.pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.pago.entity.Pago;
import com.microservicio.pago.enums.EstadoPago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByEstado(EstadoPago estado);
}
