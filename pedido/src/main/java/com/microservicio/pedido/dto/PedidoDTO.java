package com.microservicio.pedido.dto;

import com.microservicio.pedido.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {

    private Long idPedido;
    private Long idCliente;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;

    private BigDecimal subtotal;
    private BigDecimal costoDespacho;
    private BigDecimal total;

    private Long idPago;
    private Long idDespacho;

    private List<DetallePedidoDTO> detalles = new ArrayList<>();

    public PedidoDTO() {
    }

    public PedidoDTO(
            Long idPedido,
            Long idCliente,
            LocalDateTime fechaPedido,
            EstadoPedido estado,
            BigDecimal subtotal,
            BigDecimal costoDespacho,
            BigDecimal total,
            Long idPago,
            Long idDespacho,
            List<DetallePedidoDTO> detalles) {

        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.subtotal = subtotal;
        this.costoDespacho = costoDespacho;
        this.total = total;
        this.idPago = idPago;
        this.idDespacho = idDespacho;
        this.detalles = detalles;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getCostoDespacho() {
        return costoDespacho;
    }

    public void setCostoDespacho(BigDecimal costoDespacho) {
        this.costoDespacho = costoDespacho;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Long getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(Long idDespacho) {
        this.idDespacho = idDespacho;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }
}