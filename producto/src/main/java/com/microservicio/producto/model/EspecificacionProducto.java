package com.microservicio.producto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    name = "especificaciones_producto",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"producto_id", "atributo_id"}
    )
)
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EspecificacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEspecificacionProducto;

    @NotBlank(message = "El valor de la especificación es obligatorio")
    @Size(
        max = 500,
        message = "El valor no puede superar los 500 caracteres"
    )
    @Column(nullable = false, length = 500)
    private String valor;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Producto producto;

    @NotNull(message = "La definición del atributo es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atributo_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DefinicionAtributo atributo;
}