package com.microservicio.despacho.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.exception.ReglaNegocioException;

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
@Table(name = "tarifa_despacho")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarifaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long idTarifa;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadEntrega modalidad;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    /** Nula = vigencia indefinida. */
    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    /** Una zona puede tener muchas tarifas ("aplica" en el diagrama). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zona", nullable = false)
    private ZonaCobertura zona;

    public TarifaDespacho(ZonaCobertura zona, BigDecimal monto, ModalidadEntrega modalidad,
            LocalDate vigenciaDesde, LocalDate vigenciaHasta) {
        if (vigenciaHasta != null && vigenciaHasta.isBefore(vigenciaDesde)) {
            throw new ReglaNegocioException("vigenciaHasta no puede ser anterior a vigenciaDesde");
        }
        this.zona = zona;
        this.monto = monto;
        this.modalidad = modalidad;
        this.vigenciaDesde = vigenciaDesde;
        this.vigenciaHasta = vigenciaHasta;
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(vigenciaDesde) && (vigenciaHasta == null || !fecha.isAfter(vigenciaHasta));
    }

    public BigDecimal calcularCosto() {
        return monto;
    }
}
