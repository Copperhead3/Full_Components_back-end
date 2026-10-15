package com.microservicio.Usuarios.repository;

import com.microservicio.Usuarios.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface TokenRecuperacionRepository
        extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByToken(String token);
    List<TokenRecuperacion> findByUsuarioIdUsuario(Long idUsuario);
}