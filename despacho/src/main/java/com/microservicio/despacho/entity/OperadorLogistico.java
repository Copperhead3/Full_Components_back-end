package com.microservicio.despacho.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operador_logistico")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperadorLogistico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operador")
    private Long idOperador;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String contacto;

    @Column(name = "codigo_operador", nullable = false, unique = true, length = 50)
    private String codigoOperador;

    @OneToMany(mappedBy = "operador", fetch = FetchType.LAZY)
    private List<Despacho> despachos = new ArrayList<>();

    public OperadorLogistico(String nombre, String contacto, String codigoOperador) {
        this.nombre = nombre.trim();
        this.contacto = contacto.trim();
        this.codigoOperador = codigoOperador.trim().toUpperCase();
    }

    /** Asigna este operador al despacho (la logica de validacion vive en Despacho.asignarOperador). */
    public void asignarDespacho(Despacho despacho) {
        despacho.asignarOperador(this);
    }

    /** Resumen de los numeros de seguimiento de los despachos activos del operador. */
    public String obtenerSeguimiento() {
        String activos = despachos.stream()
                .filter(d -> !d.getEstado().esFinal())
                .map(Despacho::getNumeroSeguimiento)
                .collect(Collectors.joining(", "));
        return activos.isEmpty() ? "Sin despachos activos" : "Despachos activos: " + activos;
    }
}
