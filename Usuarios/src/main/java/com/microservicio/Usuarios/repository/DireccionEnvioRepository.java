package com.microservicio.Usuarios.repository;

import com.microservicio.Usuarios.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Long> {

    List<DireccionEnvio> findByClienteIdUsuario(Long idUsuario);

    Optional<DireccionEnvio> findByClienteIdUsuarioAndPredeterminadaTrue(Long idUsuario);
}