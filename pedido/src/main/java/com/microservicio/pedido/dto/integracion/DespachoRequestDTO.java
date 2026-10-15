package com.microservicio.pedido.dto.integracion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class DespachoRequestDTO {

    @NotNull(message = "La modalidad de entrega es obligatoria")
    private ModalidadEntregaDTO modalidad;

    @Valid
    private DireccionDespachoDTO direccion;

    public DespachoRequestDTO() {
    }

    public DespachoRequestDTO(
            ModalidadEntregaDTO modalidad,
            DireccionDespachoDTO direccion) {

        this.modalidad = modalidad;
        this.direccion = direccion;
    }

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