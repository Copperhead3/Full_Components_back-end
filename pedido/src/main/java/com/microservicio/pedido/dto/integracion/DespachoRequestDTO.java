package com.microservicio.pedido.dto.integracion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class DespachoRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "La modalidad de entrega es obligatoria")
    private ModalidadEntregaDTO modalidad;

    @Valid
    private DireccionDespachoDTO direccion;

    public DespachoRequestDTO() {
    }

    public DespachoRequestDTO(
            Long idPedido,
            ModalidadEntregaDTO modalidad,
            DireccionDespachoDTO direccion) {

        this.idPedido = idPedido;
        this.modalidad = modalidad;
        this.direccion = direccion;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public ModalidadEntregaDTO getModalidad() {
        return modalidad;
    }

    public void setModalidad(
            ModalidadEntregaDTO modalidad) {
        this.modalidad = modalidad;
    }

    public DireccionDespachoDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(
            DireccionDespachoDTO direccion) {
        this.direccion = direccion;
    }
}