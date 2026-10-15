package com.microservicio.producto.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import com.microservicio.producto.dto.StockDTO;

@Service
public class InventarioClientService {
    private final RestClient inventarioRestClient;

    public InventarioClientService(
            @Qualifier("inventarioRestClient") RestClient inventarioRestClient) {
        this.inventarioRestClient = inventarioRestClient;
    }

    public StockDTO consultarStock(Long idProducto) {
        if (idProducto == null || idProducto <= 0)
            throw new IllegalArgumentException("El ID del producto debe ser mayor que cero");
        try {
            StockDTO stock = inventarioRestClient.get()
                .uri("/api/v1/inventario/productos/{idProducto}", idProducto)
                .retrieve().body(StockDTO.class);

            if (stock == null || !idProducto.equals(stock.getIdProducto())
                    || stock.getCantidadDisponible() == null || stock.getCantidadDisponible() < 0) {
                return sinInformacion(idProducto, "Inventario no devolvió una disponibilidad válida");
            }
            stock.setStockConsultado(true);
            stock.setMensajeStock(stock.getCantidadDisponible() > 0
                ? "Stock disponible" : "Sin stock disponible");
            return stock;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404)
                return sinInformacion(idProducto, "El producto todavía no tiene stock registrado");
            return sinInformacion(idProducto, "No fue posible consultar el stock en inventario");
        } catch (RestClientException ex) {
            return sinInformacion(idProducto, "Inventario no está disponible. Intenta nuevamente más tarde");
        }
    }

    private StockDTO sinInformacion(Long idProducto, String mensaje) {
        StockDTO stock = new StockDTO();
        stock.setIdProducto(idProducto);
        stock.setCantidadDisponible(null);
        stock.setStockConsultado(false);
        stock.setMensajeStock(mensaje);
        return stock;
    }
}
