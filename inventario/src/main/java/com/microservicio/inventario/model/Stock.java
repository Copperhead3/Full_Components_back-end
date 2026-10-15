package com.microservicio.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = "id_producto"))
@Data
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStock;

    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser mayor que cero")
    @Column(name = "id_producto", nullable = false, unique = true)
    private Long idProducto;

    @NotNull(message = "La cantidad física es obligatoria")
    @PositiveOrZero(message = "La cantidad física no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidadFisica = 0;

    @NotNull(message = "La cantidad reservada es obligatoria")
    @PositiveOrZero(message = "La cantidad reservada no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidadReservada = 0;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    @Column(nullable = false)
    private Integer stockMinimo = 0;

    @Version
    private Long version;
}
