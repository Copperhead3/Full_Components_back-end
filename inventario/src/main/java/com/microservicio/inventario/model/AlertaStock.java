package com.microservicio.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "alerta_stock")
@Data
@NoArgsConstructor
public class AlertaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlertaStock;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAlerta estadoAlerta = EstadoAlerta.ACTIVA;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidadDetectada;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaCreacionAlerta;

    private LocalDateTime resueltaEn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Stock stock;
}
