package com.microservicio.pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicio.pago.entity.TransaccionPago;

@Repository
public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {

    List<TransaccionPago> findByPagoIdPagoOrderByIdTransaccionAsc(Long idPago);
}
