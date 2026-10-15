package com.microservicio.despacho.entity;

import java.time.LocalDateTime;

import com.microservicio.despacho.enums.EstadoDespacho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historial_estado_despacho")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistorialEstadoDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoDespacho estado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_despacho", nullable = false)
    private Despacho despacho;

    /** Solo lo crea Despacho (composicion). */
    HistorialEstadoDespacho(Despacho despacho, EstadoDespacho estado, String observacion) {
        this.despacho = despacho;
        this.estado = estado;
        this.observacion = observacion;
    }

    /** Sella la fecha del cambio y lo agrega al historial del despacho. */
    public void registrarCambio() {
        this.fecha = LocalDateTime.now();
        despacho.getHistorial().add(this);
    }
}
