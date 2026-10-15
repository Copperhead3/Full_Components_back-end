package com.microservicio.despacho.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.despacho.dto.ActualizarEstadoRequest;
import com.microservicio.despacho.dto.DespachoRequest;
import com.microservicio.despacho.dto.DespachoResponse;
import com.microservicio.despacho.dto.HistorialEstadoResponse;
import com.microservicio.despacho.dto.SeguimientoResponse;
import com.microservicio.despacho.entity.Despacho;
import com.microservicio.despacho.entity.DireccionEntrega;
import com.microservicio.despacho.entity.OperadorLogistico;
import com.microservicio.despacho.entity.TarifaDespacho;
import com.microservicio.despacho.entity.ZonaCobertura;
import com.microservicio.despacho.enums.EstadoDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.exception.RecursoNoEncontradoException;
import com.microservicio.despacho.exception.ReglaNegocioException;
import com.microservicio.despacho.repository.DespachoRepository;
import com.microservicio.despacho.repository.OperadorLogisticoRepository;
import com.microservicio.despacho.repository.TarifaDespachoRepository;
import com.microservicio.despacho.repository.ZonaCoberturaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final ZonaCoberturaRepository zonaRepository;
    private final TarifaDespachoRepository tarifaRepository;
    private final OperadorLogisticoRepository operadorRepository;

    @Value("${despacho.dias-estimados-domicilio:3}")
    private int diasEstimadosDomicilio;

    @Value("${despacho.dias-estimados-retiro:1}")
    private int diasEstimadosRetiro;

    @Transactional
    public DespachoResponse crear(DespachoRequest request) {
        ModalidadEntrega modalidad = request.modalidad();
        DireccionEntrega direccion = request.direccion() == null ? null : request.direccion().toEntity();
        TarifaDespacho tarifa = null;

        if (modalidad == ModalidadEntrega.DOMICILIO) {
            if (direccion == null || !direccion.estaCompleta()) {
                throw new ReglaNegocioException("Un despacho a domicilio requiere una direccion completa");
            }
            ZonaCobertura zona = zonaRepository
                    .findByRegionIgnoreCaseAndComunaIgnoreCase(direccion.getRegion(), direccion.getComuna())
                    .filter(z -> z.cubre(direccion))
                    .orElseThrow(() -> new ReglaNegocioException("La direccion no esta dentro de una zona de cobertura activa"));
            LocalDate hoy = LocalDate.now();
            tarifa = tarifaRepository.findByZonaIdZonaAndModalidad(zona.getIdZona(), modalidad).stream()
                    .filter(t -> t.estaVigente(hoy))
                    .max(Comparator.comparing(TarifaDespacho::getVigenciaDesde))
                    .orElseThrow(() -> new ReglaNegocioException("No hay una tarifa vigente para la zona y modalidad indicadas"));
        }

        int dias = modalidad == ModalidadEntrega.DOMICILIO ? diasEstimadosDomicilio : diasEstimadosRetiro;
        Despacho despacho = new Despacho(request.idPedido(), modalidad, direccion, generarNumeroSeguimiento(),
                LocalDateTime.now().plusDays(dias));
        despacho.calcularCosto(tarifa);
        despacho.registrarSeguimiento("Despacho creado");
        return DespachoResponse.from(despachoRepository.save(despacho));
    }

    @Transactional(readOnly = true)
    public DespachoResponse obtener(Long idDespacho) {
        return DespachoResponse.from(buscar(idDespacho));
    }

    @Transactional(readOnly = true)
    public List<DespachoResponse> listar(EstadoDespacho estado) {
        List<Despacho> despachos = estado == null ? despachoRepository.findAll() : despachoRepository.findByEstado(estado);
        return despachos.stream().map(DespachoResponse::from).toList();
    }

    @Transactional
    public DespachoResponse asignarOperador(Long idDespacho, Long idOperador) {
        Despacho despacho = buscar(idDespacho);
        OperadorLogistico operador = operadorRepository.findById(idOperador)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el operador con id " + idOperador));
        operador.asignarDespacho(despacho);
        return DespachoResponse.from(despacho);
    }

    @Transactional
    public DespachoResponse actualizarEstado(Long idDespacho, ActualizarEstadoRequest request) {
        Despacho despacho = buscar(idDespacho);
        despacho.actualizarEstado(request.estado(), request.observacion());
        return DespachoResponse.from(despacho);
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoResponse> historial(Long idDespacho) {
        return buscar(idDespacho).getHistorial().stream().map(HistorialEstadoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SeguimientoResponse seguimiento(String numeroSeguimiento) {
        Despacho despacho = despachoRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un despacho con numero de seguimiento " + numeroSeguimiento));
        return SeguimientoResponse.from(despacho);
    }

    private Despacho buscar(Long idDespacho) {
        return despachoRepository.findById(idDespacho)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el despacho con id " + idDespacho));
    }

    private String generarNumeroSeguimiento() {
        return "DSP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
