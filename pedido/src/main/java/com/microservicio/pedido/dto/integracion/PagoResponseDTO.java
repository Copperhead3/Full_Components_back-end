package com.microservicio.pedido.dto.integracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponseDTO {

    private Long idPago;

    private BigDecimal monto;

    private String moneda;

    private LocalDateTime fechaCreacion;

    private String estado;

    private MetodoPagoDTO metodo;

    private BigDecimal montoReembolsado;

    private BigDecimal montoDisponibleReembolso;


    // =========================================================
    // CONSTRUCTOR VACÍO
    // =========================================================

    public PagoResponseDTO() {
    }


    // =========================================================
    // CONSTRUCTOR COMPLETO
    // =========================================================

    public PagoResponseDTO(
            Long idPago,
            BigDecimal monto,
            String moneda,
            LocalDateTime fechaCreacion,
            String estado,
            MetodoPagoDTO metodo,
            BigDecimal montoReembolsado,
            BigDecimal montoDisponibleReembolso) {

        this.idPago = idPago;
        this.monto = monto;
        this.moneda = moneda;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.metodo = metodo;
        this.montoReembolsado = montoReembolsado;
        this.montoDisponibleReembolso = montoDisponibleReembolso;
    }


    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public MetodoPagoDTO getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPagoDTO metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getMontoReembolsado() {
        return montoReembolsado;
    }

    public void setMontoReembolsado(BigDecimal montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }

    public BigDecimal getMontoDisponibleReembolso() {
        return montoDisponibleReembolso;
    }

    public void setMontoDisponibleReembolso(
            BigDecimal montoDisponibleReembolso) {

        this.montoDisponibleReembolso = montoDisponibleReembolso;
    }
}