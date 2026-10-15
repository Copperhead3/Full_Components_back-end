package com.microservicio.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "reserva_stock", uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "referencia_compra"}))
@Data
@NoArgsConstructor
public class ReservaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservaStock;

    @NotBlank(message = "La referencia de compra es obligatoria")
    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    @Column(name = "referencia_compra", nullable = false, length = 100)
    private String referenciaCompra;

    @NotNull(message = "La cantidad de reserva es obligatoria")
    @Positive(message = "La cantidad de reserva debe ser mayor que cero")
    @Column(nullable = false)
    private Integer cantidadReserva;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estadoReserva = EstadoReserva.ACTIVA;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaCreacionReserva;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime venceEn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Stock stock;

    @Version
    private Long version;
}
