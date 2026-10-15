package com.microservicio.Carrito.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @NotNull(message = "El cliente es obligatorio")
    @Column(nullable = false, unique = true)
    private Long idCliente;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(
        mappedBy = "carrito",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ItemCarrito> items = new ArrayList<>();


    // Constructor vacío requerido por JPA
    public Carrito() {
    }


    // Asigna automáticamente la fecha al crear el carrito
    @PrePersist
    public void asignarFechaCreacion() {
        this.fechaCreacion = LocalDateTime.now();
    }


    // Calcula el subtotal sumando todos los items
    public BigDecimal calcularSubtotal() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Permite saber si el carrito no contiene productos
    public boolean estaVacio() {
        return items.isEmpty();
    }


    // Agrega un item y mantiene la relación bidireccional
    public void agregarItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }


    // Elimina un item y rompe correctamente la relación
    public void eliminarItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }


    // Elimina todos los items del carrito
    public void vaciar() {
        for (ItemCarrito item : items) {
            item.setCarrito(null);
        }

        items.clear();
    }


    // Getters y Setters

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }
}