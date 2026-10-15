package com.microservicio.pago.gateway;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservicio.pago.enums.EstadoPago;
import com.microservicio.pago.exception.PasarelaException;

/**
 * Pasarela local para desarrollo y pruebas (pasarela.modo=simulada, valor por defecto).
 * Aprueba los pagos cuyo monto no supera "pasarela.simulada.monto-maximo"; el resto se rechaza.
 */
@Component
@ConditionalOnProperty(name = "pasarela.modo", havingValue = "simulada", matchIfMissing = true)
public class PasarelaPagoSimulada implements PasarelaPago {

    private final Map<String, ResultadoPago> transacciones = new ConcurrentHashMap<>();
    private final BigDecimal montoMaximo;

    public PasarelaPagoSimulada(@Value("${pasarela.simulada.monto-maximo:1000000}") BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    @Override
    public ResultadoPago crearTransaccion(CrearTransaccionRequest datos) {
        boolean aprobado = datos.monto().compareTo(montoMaximo) <= 0;
        ResultadoPago resultado = new ResultadoPago(
                "SIM-" + UUID.randomUUID(),
                aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO,
                aprobado ? "Transaccion aprobada (simulada)" : "Monto excede el maximo permitido (" + montoMaximo + ")");
        transacciones.put(resultado.codigoProveedor(), resultado);
        return resultado;
    }

    @Override
    public ResultadoPago consultarTransaccion(String id) {
        ResultadoPago resultado = transacciones.get(id);
        if (resultado == null) {
            throw new PasarelaException("La pasarela no conoce la transaccion " + id);
        }
        return resultado;
    }

    @Override
    public ResultadoReembolso ejecutarReembolso(EjecutarReembolsoRequest datos) {
        ResultadoPago original = transacciones.get(datos.codigoProveedor());
        if (original == null || original.estado() != EstadoPago.APROBADO) {
            return new ResultadoReembolso(false, null, "Transaccion original no encontrada o no aprobada");
        }
        return new ResultadoReembolso(true, "SIM-RF-" + UUID.randomUUID(), "Reembolso aprobado (simulado)");
    }
}
