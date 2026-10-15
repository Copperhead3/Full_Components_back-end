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
@Table(name = "imagenes_producto")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagenProducto;

    @NotBlank(message = "La ruta de la imagen es obligatoria")
    @Size(
        max = 600,
        message = "La ruta no puede superar los 600 caracteres"
    )
    @Column(nullable = false, length = 600)
    private String url;

    @NotBlank(message = "El formato de la imagen es obligatorio")
    @Pattern(
        regexp = "JPG|JPEG|PNG|WEBP",
        message = "El formato debe ser JPG, JPEG, PNG o WEBP"
    )
    @Column(nullable = false, length = 10)
    private String formato;

    @NotNull(message = "El orden de la imagen es obligatorio")
    @Min(value = 0, message = "El orden no puede ser negativo")
    @Column(nullable = false)
    private Integer orden = 0;

    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal = false;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Producto producto;
}