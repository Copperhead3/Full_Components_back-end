package com.microservicio.Carrito.dto.integracion;

import java.math.BigDecimal;

public class ProductoResponseDTO {
    private Long idProducto;
    private String nombreProducto;
    private BigDecimal precioVigente;
    private String estado;

    public ProductoResponseDTO() {}
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public BigDecimal getPrecioVigente() { return precioVigente; }
    public void setPrecioVigente(BigDecimal precioVigente) { this.precioVigente = precioVigente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
