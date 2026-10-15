package com.microservicio.Usuarios.service;

import com.microservicio.Usuarios.model.Usuario;
import com.microservicio.Usuarios.model.TokenRecuperacion;
import com.microservicio.Usuarios.repository.UsuarioRepository;
import com.microservicio.Usuarios.repository.TokenRecuperacionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;

    public AuthService(
            UsuarioRepository usuarioRepository,
            TokenRecuperacionRepository tokenRepository) {

        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
    }

    // INICIAR SESIÓN
    public Usuario iniciarSesion(
            String correo,
            String contrasena) {

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Correo o contraseña incorrectos"
                        )
                );

        if (!usuario.getContrasena().equals(contrasena)) {

            throw new RuntimeException(
                    "Correo o contraseña incorrectos"
            );
        }

        return usuario;
    }

    // GENERAR TOKEN PARA RECUPERAR CONTRASEÑA
    public TokenRecuperacion generarTokenRecuperacion(
            String correo) {

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        TokenRecuperacion token =
                new TokenRecuperacion();

        token.setToken(UUID.randomUUID().toString());

        token.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(30)
        );

        token.setUtilizado(false);
        token.setUsuario(usuario);

        return tokenRepository.save(token);
    }

    // RESTABLECER CONTRASEÑA CON TOKEN
    public void restablecerContrasena(
            String token,
            String nuevaContrasena) {

        TokenRecuperacion tokenRecuperacion =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Token no válido"
                                )
                        );

        if (!tokenRecuperacion.esValido()) {

            throw new RuntimeException(
                    "El token ha expirado o ya fue utilizado"
            );
        }

        Usuario usuario =
                tokenRecuperacion.getUsuario();

        usuario.setContrasena(nuevaContrasena);

        usuarioRepository.save(usuario);

        tokenRecuperacion.marcarComoUtilizado();

        tokenRepository.save(tokenRecuperacion);
    }

    // CAMBIAR CONTRASEÑA CONOCIENDO LA ACTUAL
    public void cambiarContrasena(
            Long idUsuario,
            String contrasenaActual,
            String nuevaContrasena) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        if (!usuario.getContrasena()
                .equals(contrasenaActual)) {

            throw new RuntimeException(
                    "La contraseña actual es incorrecta"
            );
        }

        usuario.setContrasena(nuevaContrasena);

        usuarioRepository.save(usuario);
    }
}