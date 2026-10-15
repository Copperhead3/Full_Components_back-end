package com.microservicio.pago.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicio.pago.dto.TransaccionPagoResponse;
import com.microservicio.pago.entity.Pago;
import com.microservicio.pago.entity.TransaccionPago;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.exception.RecursoNoEncontradoException;
import com.microservicio.pago.gateway.PasarelaPago;
import com.microservicio.pago.gateway.ResultadoPago;
import com.microservicio.pago.repository.TransaccionPagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransaccionPagoService {

    private final TransaccionPagoRepository transaccionRepository;
    private final PasarelaPago pasarela;

    @Transactional(readOnly = true)
    public TransaccionPagoResponse obtener(Long idTransaccion) {
        return TransaccionPagoResponse.from(buscar(idTransaccion));
    }

    /**
     * Si la transaccion sigue PENDIENTE y ya tiene codigo de proveedor, consulta a la pasarela,
     * actualiza la transaccion y, de ser necesario, el pago asociado.
     */
    @Transactional
    public TransaccionPagoResponse consultarEstado(Long idTransaccion) {
        TransaccionPago transaccion = buscar(idTransaccion);

        if (transaccion.consultarEstado() == EstadoPago.PENDIENTE && transaccion.getCodigoProveedor() != null) {
            ResultadoPago resultado = pasarela.consultarTransaccion(transaccion.getCodigoProveedor());
            transaccion.registrarResultado(resultado.estado(), transaccion.getCodigoProveedor(), resultado.respuesta());

            Pago pago = transaccion.getPago();
            if (pago.getEstado() == EstadoPago.PENDIENTE) {
                pago.confirmarResultado();
            }
        }
        return TransaccionPagoResponse.from(transaccion);
    }

    private TransaccionPago buscar(Long idTransaccion) {
        return transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la transaccion con id " + idTransaccion));
    }
}
