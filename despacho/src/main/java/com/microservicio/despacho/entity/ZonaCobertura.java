package com.microservicio.despacho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zona_cobertura", uniqueConstraints = @UniqueConstraint(columnNames = { "region", "comuna" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ZonaCobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zona")
    private Long idZona;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 100)
    private String comuna;

    @Column(nullable = false)
    private boolean activa;

    public ZonaCobertura(String region, String comuna, boolean activa) {
        this.region = region.trim();
        this.comuna = comuna.trim();
        this.activa = activa;
    }

    /** La zona cubre la direccion si esta activa y coinciden region y comuna (sin distinguir mayusculas). */
    public boolean cubre(DireccionEntrega direccion) {
        return direccion != null
                && activa
                && region.equalsIgnoreCase(direccion.getRegion())
                && comuna.equalsIgnoreCase(direccion.getComuna());
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }
}
