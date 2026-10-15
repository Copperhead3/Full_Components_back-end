package com.microservicio.despacho;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.microservicio.despacho.dto.ActualizarEstadoRequest;
import com.microservicio.despacho.dto.DespachoRequest;
import com.microservicio.despacho.dto.DespachoResponse;
import com.microservicio.despacho.dto.DireccionEntregaRequest;
import com.microservicio.despacho.dto.OperadorLogisticoRequest;
import com.microservicio.despacho.dto.OperadorLogisticoResponse;
import com.microservicio.despacho.dto.TarifaDespachoRequest;
import com.microservicio.despacho.dto.ZonaCoberturaRequest;
import com.microservicio.despacho.dto.ZonaCoberturaResponse;
import com.microservicio.despacho.enums.EstadoDespacho;
import com.microservicio.despacho.enums.ModalidadEntrega;
import com.microservicio.despacho.exception.ReglaNegocioException;
import com.microservicio.despacho.service.DespachoService;
import com.microservicio.despacho.service.OperadorLogisticoService;
import com.microservicio.despacho.service.TarifaDespachoService;
import com.microservicio.despacho.service.ZonaCoberturaService;

@SpringBootTest
class DespachoFlujoTest {

    @Autowired
    DespachoService despachoService;
    @Autowired
    ZonaCoberturaService zonaService;
    @Autowired
    TarifaDespachoService tarifaService;
    @Autowired
    OperadorLogisticoService operadorService;

    private ZonaCoberturaResponse zonaConTarifa(String comuna, String monto) {
        ZonaCoberturaResponse zona = zonaService.crear(new ZonaCoberturaRequest("Biobio", comuna, true));
        tarifaService.crear(new TarifaDespachoRequest(zona.idZona(), new BigDecimal(monto),
                ModalidadEntrega.DOMICILIO, LocalDate.now().minusDays(1), null));
        return zona;
    }

    private DespachoRequest domicilio(String comuna) {
        return new DespachoRequest(ModalidadEntrega.DOMICILIO,
                new DireccionEntregaRequest("Biobio", comuna, "Barros Arana", "123", null, null));
    }

    private DespachoResponse cambiar(Long id, EstadoDespacho estado) {
        return despachoService.actualizarEstado(id, new ActualizarEstadoRequest(estado, null));
    }

    @Test
    void flujoCompletoADomicilio() {
        zonaConTarifa("Concepcion", "3500.00");
        OperadorLogisticoResponse operador = operadorService
                .crear(new OperadorLogisticoRequest("Operador Uno", "uno@correo.cl", "op-001"));

        DespachoResponse d = despachoService.crear(domicilio("Concepcion"));
        assertEquals(EstadoDespacho.PENDIENTE, d.estado());
        assertEquals(0, new BigDecimal("3500.00").compareTo(d.costo()));
        assertTrue(d.numeroSeguimiento().startsWith("DSP-"));

        cambiar(d.idDespacho(), EstadoDespacho.PREPARANDO);
        cambiar(d.idDespacho(), EstadoDespacho.LISTO_PARA_DESPACHO);

        // no se puede despachar sin operador
        assertThrows(ReglaNegocioException.class, () -> cambiar(d.idDespacho(), EstadoDespacho.DESPACHADO));

        despachoService.asignarOperador(d.idDespacho(), operador.idOperador());
        cambiar(d.idDespacho(), EstadoDespacho.DESPACHADO);
        cambiar(d.idDespacho(), EstadoDespacho.EN_TRANSITO);
        DespachoResponse entregado = cambiar(d.idDespacho(), EstadoDespacho.ENTREGADO);
        assertEquals(EstadoDespacho.ENTREGADO, entregado.estado());

        // creado, PREPARANDO, LISTO, operador asignado, DESPACHADO, EN_TRANSITO, ENTREGADO
        assertEquals(7, despachoService.historial(d.idDespacho()).size());
        assertEquals(7, despachoService.seguimiento(d.numeroSeguimiento()).historial().size());

        // estado final: no admite mas cambios
        assertThrows(ReglaNegocioException.class, () -> cambiar(d.idDespacho(), EstadoDespacho.CANCELADO));
    }

    @Test
    void rechazaDireccionFueraDeCoberturaOZonaInactiva() {
        assertThrows(ReglaNegocioException.class, () -> despachoService.crear(domicilio("Comuna Sin Cobertura")));

        ZonaCoberturaResponse zona = zonaConTarifa("Talcahuano", "4000.00");
        zonaService.desactivar(zona.idZona());
        assertThrows(ReglaNegocioException.class, () -> despachoService.crear(domicilio("Talcahuano")));

        zonaService.activar(zona.idZona());
        assertEquals(EstadoDespacho.PENDIENTE, despachoService.crear(domicilio("Talcahuano")).estado());
    }

    @Test
    void retiroEnTiendaNoTieneCostoNiRequiereOperador() {
        DespachoResponse d = despachoService.crear(new DespachoRequest(ModalidadEntrega.RETIRO_EN_TIENDA, null));
        assertEquals(0, BigDecimal.ZERO.compareTo(d.costo()));
        assertNull(d.direccion());

        cambiar(d.idDespacho(), EstadoDespacho.PREPARANDO);
        cambiar(d.idDespacho(), EstadoDespacho.LISTO_PARA_DESPACHO);
        assertEquals(EstadoDespacho.ENTREGADO, cambiar(d.idDespacho(), EstadoDespacho.ENTREGADO).estado());
    }

    @Test
    void noPermiteSaltarEstados() {
        zonaConTarifa("Coronel", "3000.00");
        DespachoResponse d = despachoService.crear(domicilio("Coronel"));
        assertThrows(ReglaNegocioException.class, () -> cambiar(d.idDespacho(), EstadoDespacho.ENTREGADO));
    }
}
