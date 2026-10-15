package com.microservicio.Usuarios.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Usuario {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(
        regexp = "^\\d{7,8}-[0-9kK]$",
        message = "El RUT debe tener el formato 12345678-9"
    )
    @Column(nullable = false, unique = true)
    private String rut;

    private String telefono;

    private LocalDateTime fechaRegistro;

    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DireccionEnvio> direcciones = new ArrayList<>();

    @PrePersist
    public void asignarFechaRegistro() {
        this.fechaRegistro = LocalDateTime.now();
    }

     public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<DireccionEnvio> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<DireccionEnvio> direcciones) {
        this.direcciones = direcciones;
    }
}