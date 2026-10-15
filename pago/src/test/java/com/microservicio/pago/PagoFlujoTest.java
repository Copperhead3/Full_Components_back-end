package com.microservicio.pago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.microservicio.pago.dto.PagoRequest;
import com.microservicio.pago.dto.PagoResponse;
import com.microservicio.pago.dto.ReembolsoRequest;
import com.microservicio.pago.dto.ReembolsoResponse;
import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.enums.EstadoReembolso;
import com.microservicio.pago.enums.MetodoPago;
import com.microservicio.pago.exception.ReglaNegocioException;
import com.microservicio.pago.service.PagoService;
import com.microservicio.pago.service.ReembolsoService;

@SpringBootTest
class PagoFlujoTest {

    @Autowired
    PagoService pagoService;

    @Autowired
    ReembolsoService reembolsoService;

    private PagoResponse nuevoPago(String monto) {
        return pagoService.crear(new PagoRequest(1L, new BigDecimal(monto), "clp", MetodoPago.TARJETA_CREDITO));
    }

    @Test
    void flujoCompletoConReembolsoParcialYTotal() {
        PagoResponse pago = nuevoPago("10000.00");
        assertEquals(EstadoPago.PENDIENTE, pago.estado());
        assertEquals("CLP", pago.moneda());

        pago = pagoService.iniciar(pago.idPago());
        assertEquals(EstadoPago.APROBADO, pago.estado());
        assertEquals(1, pagoService.listarTransacciones(pago.idPago()).size());

        ReembolsoResponse r1 = reembolsoService.solicitar(pago.idPago(), new ReembolsoRequest(new BigDecimal("4000.00"), "Producto danado"));
        assertEquals(EstadoReembolso.SOLICITADO, r1.estado());
        r1 = reembolsoService.procesar(r1.idReembolso());
        assertEquals(EstadoReembolso.APROBADO, r1.estado());
        assertEquals(EstadoPago.REEMBOLSO_PARCIAL, pagoService.obtener(pago.idPago()).estado());

        ReembolsoResponse r2 = reembolsoService.solicitar(pago.idPago(), new ReembolsoRequest(new BigDecimal("6000.00"), "Devolucion"));
        reembolsoService.procesar(r2.idReembolso());
        assertEquals(EstadoPago.REEMBOLSADO, pagoService.obtener(pago.idPago()).estado());
    }

    @Test
    void noPermiteReembolsarMasDeLoDisponible() {
        PagoResponse pago = pagoService.iniciar(nuevoPago("1000.00").idPago());
        assertThrows(ReglaNegocioException.class,
                () -> reembolsoService.solicitar(pago.idPago(), new ReembolsoRequest(new BigDecimal("1000.01"), "Exceso")));
    }

    @Test
    void pagoPendienteSePuedeAnularYYaNoSePuedeIniciar() {
        PagoResponse pago = nuevoPago("500.00");
        assertEquals(EstadoPago.ANULADO, pagoService.anular(pago.idPago()).estado());
        assertThrows(ReglaNegocioException.class, () -> pagoService.iniciar(pago.idPago()));
    }

    @Test
    void montoSobreElMaximoEsRechazadoPorLaPasarela() {
        PagoResponse pago = pagoService.iniciar(nuevoPago("5000000.00").idPago());
        assertEquals(EstadoPago.RECHAZADO, pago.estado());
    }
}
