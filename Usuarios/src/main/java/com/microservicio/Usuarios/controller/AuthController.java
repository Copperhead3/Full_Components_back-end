package com.microservicio.Usuarios.controller;

import com.microservicio.Usuarios.dto.CambioContrasenaDTO;
import com.microservicio.Usuarios.dto.LoginRequestDTO;
import com.microservicio.Usuarios.dto.LoginResponseDTO;
import com.microservicio.Usuarios.dto.RestablecerContrasenaDTO;
import com.microservicio.Usuarios.model.Administrador;
import com.microservicio.Usuarios.model.Cliente;
import com.microservicio.Usuarios.model.TokenRecuperacion;
import com.microservicio.Usuarios.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.microservicio.Usuarios.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Autenticación de usuarios y gestión de contraseñas")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario mediante su correo y contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos"),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> iniciarSesion(
            @Valid @RequestBody LoginRequestDTO request) {

        Usuario usuario = authService.iniciarSesion(
                request.getCorreo(),
                request.getContrasena());

        String tipoUsuario;

        if (usuario instanceof Cliente) {
            tipoUsuario = "CLIENTE";
        } else if (usuario instanceof Administrador) {
            tipoUsuario = "ADMINISTRADOR";
        } else {
            tipoUsuario = "DESCONOCIDO";
        }

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                tipoUsuario);

        return ResponseEntity.ok(response);
    }

    // SOLICITAR RECUPERACIÓN DE CONTRASEÑA
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Genera un token temporal de recuperación de contraseña para el usuario asociado al correo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token de recuperación generado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario asociado al correo")
    })
    @PostMapping("/recuperar/{correo}")
    public ResponseEntity<TokenRecuperacion> recuperarContrasena(
            @PathVariable String correo) {

        return ResponseEntity.ok(
                authService.generarTokenRecuperacion(correo));
    }

    // RESTABLECER CONTRASEÑA CON TOKEN
    @Operation(summary = "Restablecer contraseña", description = "Establece una nueva contraseña utilizando un token de recuperación válido y no utilizado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, token expirado o token ya utilizado")
    })
    @PostMapping("/restablecer")
    public ResponseEntity<String> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaDTO request) {

        authService.restablecerContrasena(
                request.getToken(),
                request.getNuevaContrasena());

        return ResponseEntity.ok(
                "Contraseña restablecida correctamente");
    }

    // CAMBIAR CONTRASEÑA CONOCIENDO LA ACTUAL
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña de un usuario verificando previamente su contraseña actual")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de contraseña inválidos"),
            @ApiResponse(responseCode = "401", description = "La contraseña actual es incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{idUsuario}/contrasena")
    public ResponseEntity<String> cambiarContrasena(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CambioContrasenaDTO request) {

        authService.cambiarContrasena(
                idUsuario,
                request.getContrasenaActual(),
                request.getNuevaContrasena());

        return ResponseEntity.ok(
                "Contraseña actualizada correctamente");
    }
}