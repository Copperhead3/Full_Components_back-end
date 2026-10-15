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
    name = "definiciones_atributo",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"categoria_id", "clave"}
    )
)
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DefinicionAtributo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDefinicionAtributo;

    @NotBlank(message = "La clave del atributo es obligatoria")
    @Size(
        max = 80,
        message = "La clave no puede superar los 80 caracteres"
    )
    @Column(nullable = false, length = 80)
    private String clave;

    @NotBlank(message = "El nombre del atributo es obligatorio")
    @Size(
        max = 120,
        message = "El nombre no puede superar los 120 caracteres"
    )
    @Column(nullable = false, length = 120)
    private String nombreDefinicionAtributo;

    @NotBlank(message = "El tipo de dato es obligatorio")
    @Pattern(
        regexp = "TEXTO|NUMERO|BOOLEANO",
        message = "El tipo debe ser TEXTO, NUMERO o BOOLEANO"
    )
    @Column(name = "tipo_dato", nullable = false, length = 20)
    private String tipoDato = "TEXTO";

    @Size(
        max = 30,
        message = "La unidad no puede superar los 30 caracteres"
    )
    @Column(length = 30)
    private String unidad;

    @Column(nullable = false)
    private boolean filtrable = true;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Categoria categoria;
}