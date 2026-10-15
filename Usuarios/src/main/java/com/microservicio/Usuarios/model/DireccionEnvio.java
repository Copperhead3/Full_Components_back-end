package com.microservicio.Usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "direcciones_envio")
@Getter
@Setter
@NoArgsConstructor
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @NotBlank(message = "La región es obligatoria")
    @Column(nullable = false)
    private String region;

    @NotBlank(message = "La comuna es obligatoria")
    @Column(nullable = false)
    private String comuna;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 100, message = "La calle no puede superar los 100 caracteres")
    @Column(nullable = false)
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 15, message = "El número no puede superar los 15 caracteres")
    @Column(nullable = false)
    private String numero;

    @Size(max = 50, message = "El departamento o casa no puede superar los 50 caracteres")
    private String deptoCasa;

    @Size(max = 150, message = "La referencia no puede superar los 150 caracteres")
    private String referencia;

    private boolean predeterminada;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDeptoCasa() {
        return deptoCasa;
    }

    public void setDeptoCasa(String deptoCasa) {
        this.deptoCasa = deptoCasa;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isPredeterminada() {
        return predeterminada;
    }

    public void setPredeterminada(boolean predeterminada) {
        this.predeterminada = predeterminada;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}