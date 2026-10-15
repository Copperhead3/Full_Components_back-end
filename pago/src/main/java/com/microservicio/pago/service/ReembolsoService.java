package com.microservicio.pago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.pago.dto.ReembolsoRequest;
import com.microservicio.pago.dto.ReembolsoResponse;
import com.microservicio.pago.entity.Pago;
import com.microservicio.pago.entity.Reembolso;
import com.microservicio.pago.entity.TransaccionPago;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.enums.EstadoReembolso;
import com.microservicio.pago.exception.RecursoNoEncontradoException;
import com.microservicio.pago.exception.ReglaNegocioException;
import com.microservicio.pago.gateway.EjecutarReembolsoRequest;
import com.microservicio.pago.gateway.PasarelaPago;
import com.microservicio.pago.gateway.ResultadoReembolso;
import com.microservicio.pago.repository.PagoRepository;
import com.microservicio.pago.repository.ReembolsoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReembolsoService {

    private final PagoRepository pagoRepository;
    private final ReembolsoRepository reembolsoRepository;
    private final PasarelaPago pasarela;

    /** Crea el reembolso en estado SOLICITADO (no llama aun a la pasarela). */
    @Transactional
    public ReembolsoResponse solicitar(Long idPago, ReembolsoRequest request) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pago con id " + idPago));
        Reembolso reembolso = pago.solicitarReembolso(request.monto(), request.motivo());
        pagoRepository.flush(); // cascade ALL: genera el id del reembolso
        return ReembolsoResponse.from(reembolso);
    }

    /** SOLICITADO -> EN_PROCESO -> (pasarela) -> APROBADO / RECHAZADO. */
    @Transactional
    public ReembolsoResponse procesar(Long idReembolso) {
        Reembolso reembolso = buscar(idReembolso);
        reembolso.procesar();

        Pago pago = reembolso.getPago();
        TransaccionPago transaccionAprobada = pago.getTransacciones().stream()
                .filter(t -> t.getEstado() == EstadoPago.APROBADO)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El pago " + pago.getIdPago() + " no tiene una transaccion aprobada para reembolsar"));

        ResultadoReembolso resultado = pasarela.ejecutarReembolso(new EjecutarReembolsoRequest(
                transaccionAprobada.getCodigoProveedor(), reembolso.getMonto(), reembolso.getMotivo()));

        reembolso.actualizarEstado(resultado.aprobado() ? EstadoReembolso.APROBADO : EstadoReembolso.RECHAZADO);
        return ReembolsoResponse.from(reembolso);
    }

    @Transactional(readOnly = true)
    public ReembolsoResponse obtener(Long idReembolso) {
        return ReembolsoResponse.from(buscar(idReembolso));
    }

    @Transactional(readOnly = true)
    public List<ReembolsoResponse> listarPorPago(Long idPago) {
        if (!pagoRepository.existsById(idPago)) {
            throw new RecursoNoEncontradoException("No existe el pago con id " + idPago);
        }
        return reembolsoRepository.findByPagoIdPagoOrderByIdReembolsoAsc(idPago).stream()
                .map(ReembolsoResponse::from)
                .toList();
    }

    private Reembolso buscar(Long idReembolso) {
        return reembolsoRepository.findById(idReembolso)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reembolso con id " + idReembolso));
    }
}
