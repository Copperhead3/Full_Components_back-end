package com.microservicio.producto.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor 
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(
        max = 120,
        message = "El nombre no puede superar los 120 caracteres"
    )
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombreCategoria;

    @OneToMany(
        mappedBy = "categoria",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<DefinicionAtributo> atributos = new ArrayList<>();
}
