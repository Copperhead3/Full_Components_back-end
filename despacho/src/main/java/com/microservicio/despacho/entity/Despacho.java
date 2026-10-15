package com.microservicio.despacho.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microservicio.despacho.enums.EstadoDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.exception.ReglaNegocioException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "despacho")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Long idDespacho;

    @Column(name = "id_pedido", nullable = false)
    private Long idPedido;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadEntrega modalidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoDespacho estado;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_estimada_entrega", nullable = false)
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "numero_seguimiento", nullable = false, unique = true, length = 30)
    private String numeroSeguimiento;

    /** Agregacion "tiene" del diagrama (nula en retiro en tienda). */
    @Embedded
    private DireccionEntrega direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador")
    private OperadorLogistico operador;

    /** Composicion "registra": el historial no existe sin el despacho. */
    @OneToMany(mappedBy = "despacho", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idHistorial ASC")
    private List<HistorialEstadoDespacho> historial = new ArrayList<>();

    public Despacho(Long idPedido, ModalidadEntrega modalidad, DireccionEntrega direccion, String numeroSeguimiento,
            LocalDateTime fechaEstimadaEntrega) {
        this.idPedido = idPedido;
        this.modalidad = modalidad;
        this.direccion = direccion;
        this.numeroSeguimiento = numeroSeguimiento;
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoDespacho.PENDIENTE;
        this.costo = BigDecimal.ZERO;
    }

    // ------------------------------------------------------------------
    // Comportamiento del diagrama
    // ------------------------------------------------------------------

    /**
     * Calcula y fija el costo. Retiro en tienda no tiene costo; a domicilio se usa la tarifa vigente
     * de la zona (consultada por el servicio).
     */
    public BigDecimal calcularCosto(TarifaDespacho tarifa) {
        if (modalidad == ModalidadEntrega.RETIRO_EN_TIENDA) {
            this.costo = BigDecimal.ZERO;
            return costo;
        }
        if (tarifa == null || tarifa.getModalidad() != modalidad || !tarifa.estaVigente(LocalDate.now())) {
            throw new ReglaNegocioException("No hay una tarifa vigente aplicable para calcular el costo");
        }
        this.costo = tarifa.calcularCosto();
        return costo;
    }

    public void asignarOperador(OperadorLogistico nuevoOperador) {
        if (nuevoOperador == null) {
            throw new ReglaNegocioException("El operador es obligatorio");
        }
        if (modalidad == ModalidadEntrega.RETIRO_EN_TIENDA) {
            throw new ReglaNegocioException("Un retiro en tienda no requiere operador logistico");
        }
        if (estado.esFinal()) {
            throw new ReglaNegocioException("No se puede asignar operador a un despacho " + estado);
        }
        this.operador = nuevoOperador;
        registrarSeguimiento("Operador asignado: " + nuevoOperador.getNombre()
                + " (" + nuevoOperador.getCodigoOperador() + ")");
    }

    public void actualizarEstado(EstadoDespacho nuevoEstado) {
        actualizarEstado(nuevoEstado, null);
    }

    public void actualizarEstado(EstadoDespacho nuevoEstado, String observacion) {
        if (nuevoEstado == null) {
            throw new ReglaNegocioException("El nuevo estado es obligatorio");
        }
        if (!transicionPermitida(nuevoEstado)) {
            throw new ReglaNegocioException("Transicion no permitida para un despacho " + modalidad
                    + ": " + estado + " -> " + nuevoEstado);
        }
        if (nuevoEstado == EstadoDespacho.DESPACHADO && operador == null) {
            throw new ReglaNegocioException("Debe asignar un operador logistico antes de despachar");
        }
        this.estado = nuevoEstado;
        registrarSeguimiento(observacion);
    }

    public void registrarSeguimiento() {
        registrarSeguimiento(null);
    }

    /** Agrega al historial una entrada con el estado actual. */
    public void registrarSeguimiento(String observacion) {
        new HistorialEstadoDespacho(this, estado, observacion).registrarCambio();
    }

    // ------------------------------------------------------------------

    private boolean transicionPermitida(EstadoDespacho destino) {
        if (modalidad == ModalidadEntrega.RETIRO_EN_TIENDA) {
            return switch (estado) {
                case PENDIENTE -> destino == EstadoDespacho.PREPARANDO || destino == EstadoDespacho.CANCELADO;
                case PREPARANDO -> destino == EstadoDespacho.LISTO_PARA_DESPACHO || destino == EstadoDespacho.CANCELADO;
                case LISTO_PARA_DESPACHO -> destino == EstadoDespacho.ENTREGADO || destino == EstadoDespacho.CANCELADO;
                default -> false;
            };
        }
        return estado.puedeTransicionarA(destino);
    }
}
