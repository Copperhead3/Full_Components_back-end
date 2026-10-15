package com.microservicio.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "movimiento_stock", uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "referencia_operacion"}))
@Data
@NoArgsConstructor
public class MovimientoStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimientoStock;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoMovimiento tipo;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidadMovimiento;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stockAnterior;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stockPosterior;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotBlank
    @Size(max = 100)
    @Column(name = "referencia_operacion", nullable = false, length = 100)
    private String referenciaOperacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Stock stock;
}
