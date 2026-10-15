package com.microservicio.Usuarios.service;

import com.microservicio.Usuarios.model.Cliente;
import com.microservicio.Usuarios.model.DireccionEnvio;
import com.microservicio.Usuarios.repository.ClienteRepository;
import com.microservicio.Usuarios.repository.DireccionEnvioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionEnvioService {

    private final DireccionEnvioRepository direccionRepository;
    private final ClienteRepository clienteRepository;

    public DireccionEnvioService(
            DireccionEnvioRepository direccionRepository,
            ClienteRepository clienteRepository) {

        this.direccionRepository = direccionRepository;
        this.clienteRepository = clienteRepository;
    }

    // AGREGAR DIRECCIÓN
    public DireccionEnvio agregarDireccion(
            Long idCliente,
            DireccionEnvio direccion) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado")
                );

        direccion.setCliente(cliente);

        List<DireccionEnvio> direcciones =
                direccionRepository.findByClienteIdUsuario(idCliente);

        // Si es la primera dirección, será predeterminada
        if (direcciones.isEmpty()) {

            direccion.setPredeterminada(true);

        } else if (direccion.isPredeterminada()) {

            quitarPredeterminadaActual(idCliente);
        }

        return direccionRepository.save(direccion);
    }

    // LISTAR DIRECCIONES DEL CLIENTE
    public List<DireccionEnvio> listarDirecciones(Long idCliente) {

        return direccionRepository
                .findByClienteIdUsuario(idCliente);
    }

    // BUSCAR UNA DIRECCIÓN
    public DireccionEnvio buscarDireccion(Long idDireccion) {

        return direccionRepository.findById(idDireccion)
                .orElseThrow(() ->
                        new RuntimeException("Dirección no encontrada")
                );
    }

    // ACTUALIZAR DIRECCIÓN
    public DireccionEnvio actualizarDireccion(
            Long idCliente,
            Long idDireccion,
            DireccionEnvio datos) {

        DireccionEnvio direccion =
                buscarDireccion(idDireccion);

        verificarPropietario(direccion, idCliente);

        direccion.setRegion(datos.getRegion());
        direccion.setComuna(datos.getComuna());
        direccion.setCalle(datos.getCalle());
        direccion.setNumero(datos.getNumero());
        direccion.setDeptoCasa(datos.getDeptoCasa());
        direccion.setReferencia(datos.getReferencia());

        return direccionRepository.save(direccion);
    }

    // ELIMINAR DIRECCIÓN
    public void eliminarDireccion(
            Long idCliente,
            Long idDireccion) {

        DireccionEnvio direccion =
                buscarDireccion(idDireccion);

        verificarPropietario(direccion, idCliente);

        direccionRepository.delete(direccion);
    }

    // ESTABLECER DIRECCIÓN PREDETERMINADA
    public DireccionEnvio establecerPredeterminada(
            Long idCliente,
            Long idDireccion) {

        DireccionEnvio direccion =
                buscarDireccion(idDireccion);

        verificarPropietario(direccion, idCliente);

        quitarPredeterminadaActual(idCliente);

        direccion.setPredeterminada(true);

        return direccionRepository.save(direccion);
    }

    // QUITAR LA DIRECCIÓN PREDETERMINADA ACTUAL
    private void quitarPredeterminadaActual(Long idCliente) {

        direccionRepository
                .findByClienteIdUsuarioAndPredeterminadaTrue(idCliente)
                .ifPresent(direccion -> {

                    direccion.setPredeterminada(false);
                    direccionRepository.save(direccion);
                });
    }

    // VERIFICAR QUE LA DIRECCIÓN PERTENEZCA AL CLIENTE
    private void verificarPropietario(
            DireccionEnvio direccion,
            Long idCliente) {

        if (!direccion.getCliente()
                .getIdUsuario()
                .equals(idCliente)) {

            throw new RuntimeException(
                    "La dirección no pertenece al cliente"
            );
        }
    }
}