package com.microservicio.pedido.dto.integracion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class PagoRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El monto debe ser mayor a cero"
    )
    private BigDecimal monto;

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(
        regexp = "^[A-Za-z]{3}$",
        message = "La moneda debe contener 3 letras"
    )
    private String moneda;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPagoDTO metodo;

    public PagoRequestDTO() {
    }

    public PagoRequestDTO(
            Long idPedido,
            BigDecimal monto,
            String moneda,
            MetodoPagoDTO metodo) {

        this.idPedido = idPedido;
        this.monto = monto;
        this.moneda = moneda;
        this.metodo = metodo;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

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

    public MetodoPagoDTO getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPagoDTO metodo) {
        this.metodo = metodo;
    }
}