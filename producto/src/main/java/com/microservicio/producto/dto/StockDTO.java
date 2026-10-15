package com.microservicio.producto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockDTO {
    private Long idProducto;
    private Integer cantidadDisponible;
    private boolean stockConsultado;
    private String mensajeStock;
}
