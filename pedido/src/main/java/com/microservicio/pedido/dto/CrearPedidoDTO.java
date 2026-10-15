package com.microservicio.pedido.dto;

import com.microservicio.pedido.dto.integracion.MetodoPagoDTO;
import com.microservicio.pedido.dto.integracion.ModalidadEntregaDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos necesarios para crear y procesar un pedido")
public class CrearPedidoDTO {

    @Schema(example = "5")
    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser mayor a 0")
    private Long idCliente;

    @Schema(
        description = "Dirección seleccionada para entrega a domicilio",
        example = "3"
    )
    @Positive(message = "El ID de la dirección debe ser mayor a 0")
    private Long idDireccion;

    @Schema(example = "DOMICILIO")
    @NotNull(message = "La modalidad de entrega es obligatoria")
    private ModalidadEntregaDTO modalidadEntrega;

    @Schema(example = "TARJETA_DEBITO")
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPagoDTO metodoPago;

    public CrearPedidoDTO() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public ModalidadEntregaDTO getModalidadEntrega() {
        return modalidadEntrega;
    }

    public void setModalidadEntrega(
            ModalidadEntregaDTO modalidadEntrega) {
        this.modalidadEntrega = modalidadEntrega;
    }

    public MetodoPagoDTO getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(
            MetodoPagoDTO metodoPago) {
        this.metodoPago = metodoPago;
    }
}