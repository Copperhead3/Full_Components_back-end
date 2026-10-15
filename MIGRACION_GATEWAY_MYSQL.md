# Migración FullComponents Backend

## Requisitos
- Java 21
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- MySQL (XAMPP) en `localhost:3306`
- Usuario MySQL por defecto: `root`, contraseña vacía (ajustar si tu XAMPP usa otra)

## Puertos
| Servicio | Puerto | Gateway |
|---|---:|---|
| Usuarios | 8081 | `/usuarios/**` |
| Carrito | 8082 | `/carrito/**` |
| Producto | 8083 | `/producto/**` |
| Inventario | 8084 | `/inventario/**` |
| Despacho | 8085 | `/despacho/**` |
| Pedido | 8086 | `/pedido/**` |
| Pago | 8087 | `/pago/**` |
| Gateway | 8090 | - |

Ningún microservicio usa el puerto 8080.

## Bases MySQL
Cada servicio mantiene su propia base:
- `fullcomponents_usuarios`
- `fullcomponents_carrito`
- `fullcomponents_producto`
- `fullcomponents_inventario`
- `fullcomponents_despacho`
- `fullcomponents_pedido`
- `fullcomponents_pago`

Las URLs usan `createDatabaseIfNotExist=true` y Hibernate `ddl-auto=update`.

## Swagger
Swagger unificado:
- `http://localhost:8090/swagger-ui.html`

También se mantiene Swagger en cada microservicio mediante `/swagger-ui.html`.

## Relaciones añadidas/mantenidas
- Pedido -> Usuarios, Carrito, Inventario, Despacho y Pago mediante OpenFeign.
- Producto <-> Inventario mantienen sus integraciones existentes y sus URLs fueron actualizadas.
- Carrito -> Producto e Inventario mediante OpenFeign para precio, nombre y disponibilidad real.
- Pago ahora almacena y recibe `idPedido`.
- Despacho ahora almacena y recibe `idPedido`.

## Orden sugerido de arranque
1. XAMPP / MySQL
2. Usuarios
3. Producto
4. Inventario
5. Carrito
6. Despacho
7. Pago
8. Pedido
9. Gateway
