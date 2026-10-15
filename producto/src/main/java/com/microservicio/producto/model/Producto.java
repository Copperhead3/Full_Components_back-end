package com.microservicio.producto.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(
        max = 250,
        message = "El nombre no puede superar los 250 caracteres"
    )
    @Column(nullable = false, length = 250)
    private String nombreProducto;

    @NotBlank(message = "La marca es obligatoria")
    @Size(
        max = 100,
        message = "La marca no puede superar los 100 caracteres"
    )
    @Column(nullable = false, length = 100)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(
        max = 150,
        message = "El modelo no puede superar los 150 caracteres"
    )
    @Column(nullable = false, length = 150)
    private String modelo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(
        max = 3000,
        message = "La descripción no puede superar los 3000 caracteres"
    )
    @Column(nullable = false, length = 3000)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
        value = "0",
        message = "El precio no puede ser negativo"
    )
    @Digits(
        integer = 12,
        fraction = 0,
        message = "El precio debe ser un entero de hasta 12 dígitos"
    )
    @Column(
        name = "precio_vigente",
        nullable = false,
        precision = 12,
        scale = 0
    )
    private BigDecimal precioVigente;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPublicacion estado = EstadoPublicacion.BORRADOR;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Categoria categoria;

    @Valid
    @OneToMany(
        mappedBy = "producto",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ImagenProducto> imagenes = new ArrayList<>();

    @Valid
    @OneToMany(
        mappedBy = "producto",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EspecificacionProducto> especificaciones =
            new ArrayList<>();

    @Valid
    @Embedded
    private Garantia garantia;

    @Valid
    @Embedded
    private InformacionDespacho despacho;

    @ManyToMany
    @JoinTable(
        name = "productos_relacionados",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "relacionado_id"),
        uniqueConstraints = @UniqueConstraint(
            columnNames = {"producto_id", "relacionado_id"}
        )
    )
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto> relacionados = new ArrayList<>();
}