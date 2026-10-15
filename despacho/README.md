# Microservicio Despacho (Spring Boot 4.1.1 / Java 21)

Implementa el diagrama de clases: `Despacho`, `DireccionEntrega`, `OperadorLogistico`,
`HistorialEstadoDespacho`, `ZonaCobertura`, `TarifaDespacho` y los enums `EstadoDespacho` y `ModalidadEntrega`.

Ejecutar: `./mvnw spring-boot:run` (puerto 8081). Consola H2: http://localhost:8081/h2-console
(JDBC URL `jdbc:h2:mem:despachodb`, usuario `sa`, sin clave).

## Endpoints
| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | /api/zonas | Crea zona de cobertura |
| GET | /api/zonas?activa= | Lista zonas |
| GET | /api/zonas/cubre?region=&comuna= | Consulta si hay cobertura |
| GET | /api/zonas/{id} | Detalle |
| POST | /api/zonas/{id}/activar · /desactivar | Cambia el estado de la zona |
| POST | /api/tarifas | Crea tarifa para una zona |
| GET | /api/tarifas?idZona= | Lista tarifas |
| GET | /api/tarifas/vigentes?idZona=&modalidad=&fecha= | Tarifas vigentes |
| GET | /api/tarifas/{id} | Detalle |
| POST | /api/operadores | Crea operador logistico |
| GET | /api/operadores · /{id} | Consulta |
| GET | /api/operadores/{id}/seguimiento | Despachos activos del operador |
| POST | /api/despachos | Crea despacho (calcula costo y fecha estimada) |
| GET | /api/despachos?estado= · /{id} | Consulta |
| GET | /api/despachos/{id}/historial | Historial de estados |
| GET | /api/despachos/seguimiento/{numero} | Seguimiento por numero |
| PUT | /api/despachos/{id}/operador | Asigna operador `{ "idOperador": 1 }` |
| PATCH | /api/despachos/{id}/estado | Cambia estado `{ "estado": "PREPARANDO", "observacion": "..." }` |

## Prueba rapida
```
curl -X POST localhost:8081/api/zonas -H 'Content-Type: application/json' \
  -d '{"region":"Biobio","comuna":"Concepcion"}'
curl -X POST localhost:8081/api/tarifas -H 'Content-Type: application/json' \
  -d '{"idZona":1,"monto":3500,"modalidad":"DOMICILIO","vigenciaDesde":"2026-01-01"}'
curl -X POST localhost:8081/api/operadores -H 'Content-Type: application/json' \
  -d '{"nombre":"Operador Uno","contacto":"uno@correo.cl","codigoOperador":"OP-001"}'
curl -X POST localhost:8081/api/despachos -H 'Content-Type: application/json' \
  -d '{"modalidad":"DOMICILIO","direccion":{"region":"Biobio","comuna":"Concepcion","calle":"Barros Arana","numero":"123"}}'
curl -X PATCH localhost:8081/api/despachos/1/estado -H 'Content-Type: application/json' -d '{"estado":"PREPARANDO"}'
```

## Estados
Domicilio: PENDIENTE -> PREPARANDO -> LISTO_PARA_DESPACHO -> DESPACHADO -> EN_TRANSITO -> ENTREGADO
(INCIDENCIA desde DESPACHADO/EN_TRANSITO; CANCELADO antes de despachar). Requiere operador para DESPACHADO.
Retiro en tienda: PENDIENTE -> PREPARANDO -> LISTO_PARA_DESPACHO -> ENTREGADO.
