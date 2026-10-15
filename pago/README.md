# Microservicio Pago (Spring Boot 4.1.1 / Java 21)

Implementa el diagrama de clases de Pago: `Pago`, `TransaccionPago`, `Reembolso`, los enums
`MetodoPago`, `EstadoPago`, `EstadoReembolso` y la interfaz `PasarelaPago`.

Ejecutar: `./mvnw spring-boot:run` — consola H2 en http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:pagodb`, usuario `sa`, sin clave).

## Endpoints
| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | /api/pagos | Crea un pago (PENDIENTE) |
| GET | /api/pagos?estado= | Lista pagos |
| GET | /api/pagos/{id} | Detalle |
| POST | /api/pagos/{id}/iniciar | Envia a la pasarela y confirma resultado |
| POST | /api/pagos/{id}/anular | Anula un pago PENDIENTE |
| GET | /api/pagos/{id}/transacciones | Transacciones del pago |
| POST | /api/pagos/{id}/reembolsos | Solicita reembolso (SOLICITADO) |
| GET | /api/pagos/{id}/reembolsos | Reembolsos del pago |
| GET | /api/transacciones/{id} | Detalle de transaccion |
| POST | /api/transacciones/{id}/consultar-estado | Sincroniza estado con la pasarela |
| GET | /api/reembolsos/{id} | Detalle de reembolso |
| POST | /api/reembolsos/{id}/procesar | Ejecuta el reembolso en la pasarela |

## Prueba rapida
```
curl -X POST localhost:8080/api/pagos -H 'Content-Type: application/json' \
  -d '{"monto":10000.00,"moneda":"CLP","metodo":"TARJETA_CREDITO"}'
curl -X POST localhost:8080/api/pagos/1/iniciar
curl -X POST localhost:8080/api/pagos/1/reembolsos -H 'Content-Type: application/json' \
  -d '{"monto":4000.00,"motivo":"Producto danado"}'
curl -X POST localhost:8080/api/reembolsos/1/procesar
```

## Pasarela
`pasarela.modo=simulada` (defecto): aprueba montos <= `pasarela.simulada.monto-maximo`.
`pasarela.modo=feign`: usa `PasarelaPagoClient` contra `pasarela.url`.
