package com.microservicio.pago.gateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Cliente HTTP (Feign) hacia el proveedor real. Ajusta las rutas al contrato de tu proveedor. */
@FeignClient(name = "pasarela-pago", url = "${pasarela.url}")
public interface PasarelaPagoClient {

    @PostMapping("/transacciones")
    ResultadoPago crear(@RequestBody CrearTransaccionRequest datos);

    @GetMapping("/transacciones/{id}")
    ResultadoPago consultar(@PathVariable("id") String id);

    @PostMapping("/reembolsos")
    ResultadoReembolso reembolsar(@RequestBody EjecutarReembolsoRequest datos);
}
