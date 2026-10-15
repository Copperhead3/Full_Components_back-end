package com.microservicio.pago.gateway;

/** Interfaz requerida por TransaccionPago (relacion "utiliza" del diagrama). */
public interface PasarelaPago {

    ResultadoPago crearTransaccion(CrearTransaccionRequest datos);

    /** @param id codigo de la transaccion asignado por el proveedor */
    ResultadoPago consultarTransaccion(String id);

    ResultadoReembolso ejecutarReembolso(EjecutarReembolsoRequest datos);
}
