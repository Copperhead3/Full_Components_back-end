package com.microservicio.pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.pago.entity.Reembolso;

@Repository
public interface ReembolsoRepository extends JpaRepository<Reembolso, Long> {

    List<Reembolso> findByPagoIdPagoOrderByIdReembolsoAsc(Long idPago);
}
