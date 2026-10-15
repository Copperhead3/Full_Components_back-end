package com.microservicio.Usuarios.service;

import com.microservicio.Usuarios.model.Cliente;
import com.microservicio.Usuarios.repository.ClienteRepository;
import com.microservicio.Usuarios.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository) {

        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Cliente registrarCliente(Cliente cliente) {

        if (usuarioRepository.existsByCorreo(cliente.getCorreo())) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }

        if (clienteRepository.existsByRut(cliente.getRut())) {
            throw new RuntimeException("El RUT ya se encuentra registrado");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {

        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado")
                );
    }

    public Cliente buscarPorRut(String rut) {

        return clienteRepository.findByRut(rut)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado")
                );
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente actualizarCliente(Long id, Cliente datosActualizados) {

        Cliente cliente = buscarPorId(id);

        cliente.setNombre(datosActualizados.getNombre());
        cliente.setApellido(datosActualizados.getApellido());
        cliente.setTelefono(datosActualizados.getTelefono());

        /*
         * Si cambia el correo, comprobamos que el nuevo
         * no pertenezca a otro usuario.
         */
        if (!cliente.getCorreo().equals(datosActualizados.getCorreo())) {

            if (usuarioRepository.existsByCorreo(datosActualizados.getCorreo())) {
                throw new RuntimeException(
                        "El correo ya se encuentra registrado"
                );
            }

            cliente.setCorreo(datosActualizados.getCorreo());
        }

        return clienteRepository.save(cliente);
    }
}