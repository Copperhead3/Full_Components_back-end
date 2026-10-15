package com.microservicio.Usuarios.dto;

public class LoginResponseDTO {

    private Long idUsuario;
    private String correo;
    private String tipoUsuario;

    public LoginResponseDTO(
            Long idUsuario,
            String correo,
            String tipoUsuario) {

        this.idUsuario = idUsuario;
        this.correo = correo;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
}