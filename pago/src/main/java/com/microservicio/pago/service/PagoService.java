package com.microservicio.pago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.pago.dto.PagoRequest;
import com.microservicio.pago.dto.PagoResponse;
import com.microservicio.pago.dto.TransaccionPagoResponse;
import com.microservicio.pago.entity.Pago;
import com.microservicio.pago.entity.TransaccionPago;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.exception.RecursoNoEncontradoException;
import com.microservicio.pago.gateway.CrearTransaccionRequest;
import com.microservicio.pago.gateway.PasarelaPago;
import com.microservicio.pago.gateway.ResultadoPago;
import com.microservicio.pago.repository.PagoRepository;
import com.microservicio.pago.repository.TransaccionPagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final TransaccionPagoRepository transaccionRepository;
    private final PasarelaPago pasarela;

    @Transactional
    public PagoResponse crear(PagoRequest request) {
        Pago pago = new Pago(request.idPedido(), request.monto(), request.moneda(), request.metodo());
        return PagoResponse.from(pagoRepository.save(pago));
    }

    @Transactional(readOnly = true)
    public PagoResponse obtener(Long idPago) {
        return PagoResponse.from(buscar(idPago));
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listar(EstadoPago estado) {
        List<Pago> pagos = estado == null ? pagoRepository.findAll() : pagoRepository.findByEstado(estado);
        return pagos.stream().map(PagoResponse::from).toList();
    }

    /** Envia el pago a la pasarela, registra la transaccion y confirma el resultado. */
    @Transactional
    public PagoResponse iniciar(Long idPago) {
        Pago pago = buscar(idPago);
        pago.iniciarPago();

        ResultadoPago resultado = pasarela.crearTransaccion(
                new CrearTransaccionRequest(pago.getIdPago(), pago.getMonto(), pago.getMoneda(), pago.getMetodo()));

        TransaccionPago transaccion = new TransaccionPago(pago.getMonto());
        pago.agregarTransaccion(transaccion);
        transaccion.registrarResultado(resultado.estado(), resultado.codigoProveedor(), resultado.respuesta());
        transaccionRepository.save(transaccion);

        pago.confirmarResultado();
        return PagoResponse.from(pago);
    }

    @Transactional
    public PagoResponse anular(Long idPago) {
        Pago pago = buscar(idPago);
        pago.anular();
        return PagoResponse.from(pago);
    }

    @Transactional(readOnly = true)
    public List<TransaccionPagoResponse> listarTransacciones(Long idPago) {
        buscar(idPago); // valida que exista
        return transaccionRepository.findByPagoIdPagoOrderByIdTransaccionAsc(idPago).stream()
                .map(TransaccionPagoResponse::from)
                .toList();
    }

    private Pago buscar(Long idPago) {
        return pagoRepository.findById(idPago)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pago con id " + idPago));
    }
}
