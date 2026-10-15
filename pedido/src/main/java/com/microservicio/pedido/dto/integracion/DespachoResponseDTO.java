package com.microservicio.pedido.dto.integracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DespachoResponseDTO {

    private Long idDespacho;
    private Long idPedido;
    private LocalDateTime fechaCreacion;
    private ModalidadEntregaDTO modalidad;
    private String estado;
    private BigDecimal costo;
    private LocalDateTime fechaEstimadaEntrega;
    private String numeroSeguimiento;
    private DireccionDespachoDTO direccion;

    public DespachoResponseDTO() {
    }

    public Long getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(Long idDespacho) {
        this.idDespacho = idDespacho;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public ModalidadEntregaDTO getModalidad() {
        return modalidad;
    }

    public void setModalidad(
            ModalidadEntregaDTO modalidad) {
        this.modalidad = modalidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public LocalDateTime getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    public void setFechaEstimadaEntrega(
            LocalDateTime fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega =
                fechaEstimadaEntrega;
    }

    public String getNumeroSeguimiento() {
        return numeroSeguimiento;
    }

    public void setNumeroSeguimiento(
            String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }

    public DireccionDespachoDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(
            DireccionDespachoDTO direccion) {
        this.direccion = direccion;
    }
}