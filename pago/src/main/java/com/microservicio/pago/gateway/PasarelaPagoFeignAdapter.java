package com.microservicio.pago.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservicio.pago.exception.PasarelaException;

import feign.FeignException;

/** Activo con pasarela.modo=feign. Traduce los errores HTTP a PasarelaException. */
@Component
@ConditionalOnProperty(name = "pasarela.modo", havingValue = "feign")
public class PasarelaPagoFeignAdapter implements PasarelaPago {

    private final PasarelaPagoClient client;

    public PasarelaPagoFeignAdapter(PasarelaPagoClient client) {
        this.client = client;
    }

    @Override
    public ResultadoPago crearTransaccion(CrearTransaccionRequest datos) {
        try {
            return client.crear(datos);
        } catch (FeignException e) {
            throw new PasarelaException("No se pudo crear la transaccion en la pasarela: " + e.getMessage(), e);
        }
    }

    @Override
    public ResultadoPago consultarTransaccion(String id) {
        try {
            return client.consultar(id);
        } catch (FeignException e) {
            throw new PasarelaException("No se pudo consultar la transaccion " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ResultadoReembolso ejecutarReembolso(EjecutarReembolsoRequest datos) {
        try {
            return client.reembolsar(datos);
        } catch (FeignException e) {
            throw new PasarelaException("No se pudo ejecutar el reembolso en la pasarela: " + e.getMessage(), e);
        }
    }
}
