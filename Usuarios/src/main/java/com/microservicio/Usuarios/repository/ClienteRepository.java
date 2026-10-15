package com.microservicio.Usuarios.repository;

import com.microservicio.Usuarios.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository 
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRut(String rut);

    boolean existsByRut(String rut);
}