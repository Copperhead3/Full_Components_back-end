package com.microservicio.inventario.service;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import com.microservicio.inventario.model.*;
import com.microservicio.inventario.repository.*;

@Service
@Transactional
public class InventarioService {
    @Autowired private StockRepository stocks;
    @Autowired private ReservaStockRepository reservas;
    @Autowired private MovimientoStockRepository movimientos;
    @Autowired private AlertaStockRepository alertas;
    @Autowired private RestClient productoRestClient;
    @Value("${inventario.reserva-minutos:15}") private long reservaMinutos;

    private void idValido(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
    }
    private void positiva(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
    }
    private void noNegativa(Integer cantidad) {
        if (cantidad == null || cantidad < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa");
    }
    private String referencia(String referencia) {
        if (referencia == null || referencia.isBlank() || referencia.trim().length() > 100)
            throw new IllegalArgumentException("La referencia es obligatoria y admite hasta 100 caracteres");
        return referencia.trim();
    }
    private int sumar(int anterior, int cambio) {
        long total = (long) anterior + cambio;
        if (total < 0 || total > Integer.MAX_VALUE) throw new IllegalArgumentException("Cantidad fuera del rango permitido");
        return (int) total;
    }
    private int disponible(Stock stock) { return stock.getCantidadFisica() - stock.getCantidadReservada(); }
    private Stock buscarStock(Long idProducto) {
        idValido(idProducto);
        return stocks.findAll().stream().filter(s -> s.getIdProducto().equals(idProducto)).findFirst()
            .orElseThrow(() -> new NoSuchElementException("No existe stock registrado para el producto: " + idProducto));
    }
    private ReservaStock buscarReserva(Long id) {
        idValido(id);
        return reservas.findById(id).orElseThrow(() -> new NoSuchElementException("Reserva no encontrada: " + id));
    }
    private void validarProducto(Long idProducto) {
        try {
            productoRestClient.get().uri("/api/v1/productos/{id}", idProducto).retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) throw new NoSuchElementException("El producto no existe en producto: " + idProducto);
            throw ex;
        }
    }
    private Map<String, Object> stockRespuesta(Stock s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idStock",s.getIdStock()); m.put("idProducto",s.getIdProducto());
        m.put("cantidadFisica",s.getCantidadFisica()); m.put("cantidadReservada",s.getCantidadReservada());
        m.put("cantidadDisponible",disponible(s)); m.put("stockMinimo",s.getStockMinimo());
        m.put("bajoStock",disponible(s) <= s.getStockMinimo()); m.put("version",s.getVersion());
        return m;
    }
    private Map<String, Object> reservaRespuesta(ReservaStock r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idReservaStock",r.getIdReservaStock()); m.put("idProducto",r.getStock().getIdProducto());
        m.put("referenciaCompra",r.getReferenciaCompra()); m.put("cantidadReserva",r.getCantidadReserva());
        m.put("estadoReserva",r.getEstadoReserva()); m.put("fechaCreacionReserva",r.getFechaCreacionReserva());
        m.put("venceEn",r.getVenceEn()); return m;
    }
    private void movimiento(Stock stock, TipoMovimiento tipo, int cantidad, int anterior, String referencia) {
        MovimientoStock m = new MovimientoStock();
        m.setStock(stock); m.setTipo(tipo); m.setCantidadMovimiento(cantidad);
        m.setStockAnterior(anterior); m.setStockPosterior(stock.getCantidadFisica());
        m.setFecha(LocalDateTime.now()); m.setReferenciaOperacion(referencia);
        movimientos.save(m);
    }
    private void evaluarAlerta(Stock stock) {
        List<AlertaStock> activas = alertas.findAll().stream().filter(a -> a.getStock().getIdStock().equals(stock.getIdStock())
            && a.getEstadoAlerta() == EstadoAlerta.ACTIVA).toList();
        if (disponible(stock) <= stock.getStockMinimo()) {
            if (activas.isEmpty()) {
                AlertaStock a = new AlertaStock(); a.setStock(stock); a.setCantidadDetectada(disponible(stock));
                a.setFechaCreacionAlerta(LocalDateTime.now()); alertas.save(a);
            } else {
                activas.forEach(a -> a.setCantidadDetectada(disponible(stock)));
            }
        } else {
            activas.forEach(a -> { a.setEstadoAlerta(EstadoAlerta.RESUELTA); a.setResueltaEn(LocalDateTime.now()); });
        }
    }
    private void expirar(Stock stock) {
        LocalDateTime ahora = LocalDateTime.now();
        List<ReservaStock> vencidas = reservas.findAll().stream().filter(r -> r.getStock().getIdStock().equals(stock.getIdStock())
            && r.getEstadoReserva() == EstadoReserva.ACTIVA && !r.getVenceEn().isAfter(ahora)).toList();
        for (ReservaStock r : vencidas) {
            stock.setCantidadReservada(stock.getCantidadReservada() - r.getCantidadReserva());
            r.setEstadoReserva(EstadoReserva.EXPIRADA);
        }
        if (!vencidas.isEmpty()) evaluarAlerta(stock);
    }
    public Map<String, Object> registrar(Long idProducto, Integer cantidad, Integer minimo) {
        idValido(idProducto); noNegativa(cantidad); noNegativa(minimo);
        if (stocks.findAll().stream().anyMatch(s -> s.getIdProducto().equals(idProducto)))
            throw new IllegalStateException("El producto ya tiene stock registrado");
        validarProducto(idProducto);
        Stock s = new Stock(); s.setIdProducto(idProducto); s.setCantidadFisica(cantidad); s.setStockMinimo(minimo);
        stocks.saveAndFlush(s);
        movimiento(s,TipoMovimiento.REGISTRO,cantidad,0,"registro:" + idProducto);
        evaluarAlerta(s); return stockRespuesta(s);
    }
    public List<Map<String, Object>> listar() {
        List<Stock> lista = stocks.findAll();
        lista.forEach(this::expirar);
        return lista.stream().map(this::stockRespuesta).toList();
    }
    public Map<String, Object> consultar(Long idProducto) {
        Stock s = buscarStock(idProducto); expirar(s); return stockRespuesta(s);
    }
    public Map<String, Object> validarCantidad(Long idProducto, Integer cantidad) {
        positiva(cantidad); Stock s = buscarStock(idProducto); expirar(s);
        Map<String,Object> respuesta = stockRespuesta(s);
        respuesta.put("cantidadSolicitada", cantidad); respuesta.put("disponibleParaCompra", disponible(s) >= cantidad);
        return respuesta;
    }
    public Map<String,Object> cambiarMinimo(Long idProducto, Integer minimo) {
        noNegativa(minimo); Stock s = buscarStock(idProducto); expirar(s);
        s.setStockMinimo(minimo); evaluarAlerta(s); stocks.flush(); return stockRespuesta(s);
    }
    public Map<String,Object> ajustar(Long idProducto, Integer cantidad, String referencia, boolean entrada) {
        if (entrada) positiva(cantidad); else noNegativa(cantidad);
        String ref = referencia(referencia);
        if (ref.startsWith("registro:") || ref.startsWith("venta-reserva:"))
            throw new IllegalArgumentException("Ese prefijo está reservado para movimientos internos");
        Stock s = buscarStock(idProducto); expirar(s);
        Optional<MovimientoStock> previa = movimientos.findAll().stream().filter(m -> m.getStock().getIdStock().equals(s.getIdStock())
            && m.getReferenciaOperacion().equals(ref)).findFirst();
        if (previa.isPresent()) {
            MovimientoStock m = previa.get();
            boolean mismo = entrada ? m.getTipo() == TipoMovimiento.ENTRADA && m.getCantidadMovimiento().equals(cantidad)
                : (m.getTipo() == TipoMovimiento.AJUSTE_POSITIVO || m.getTipo() == TipoMovimiento.AJUSTE_NEGATIVO)
                    && m.getStockPosterior().equals(cantidad);
            if (!mismo) throw new IllegalStateException("La referencia ya identifica otra operación");
            return stockRespuesta(s);
        }
        int anterior = s.getCantidadFisica(); int posterior = entrada ? sumar(anterior, cantidad) : cantidad;
        if (posterior < s.getCantidadReservada()) throw new IllegalStateException("La cantidad física no puede ser menor que la reservada");
        s.setCantidadFisica(posterior);
        TipoMovimiento tipo = entrada ? TipoMovimiento.ENTRADA : posterior >= anterior ? TipoMovimiento.AJUSTE_POSITIVO : TipoMovimiento.AJUSTE_NEGATIVO;
        movimiento(s,tipo,entrada ? cantidad : Math.abs(posterior-anterior),anterior,ref);
        evaluarAlerta(s); stocks.flush(); return stockRespuesta(s);
    }
    public Map<String,Object> reservar(Long idProducto, Integer cantidad, String referenciaCompra) {
        positiva(cantidad); String ref = referencia(referenciaCompra);
        if (reservaMinutos <= 0) throw new IllegalStateException("El tiempo de reserva debe ser positivo");
        Stock s = buscarStock(idProducto); expirar(s);
        Optional<ReservaStock> previa = reservas.findAll().stream().filter(r -> r.getStock().getIdStock().equals(s.getIdStock())
            && r.getReferenciaCompra().equals(ref)).findFirst();
        if (previa.isPresent()) {
            ReservaStock r = previa.get();
            if (!r.getCantidadReserva().equals(cantidad)) throw new IllegalStateException("La referencia ya usa otra cantidad");
            // Retry returns the original reservation and never reserves twice.
            return reservaRespuesta(r);
        }
        if (disponible(s) < cantidad) throw new IllegalStateException("Stock insuficiente para reservar");
        s.setCantidadReservada(sumar(s.getCantidadReservada(),cantidad));
        ReservaStock r = new ReservaStock(); r.setStock(s); r.setCantidadReserva(cantidad); r.setReferenciaCompra(ref);
        r.setFechaCreacionReserva(LocalDateTime.now()); r.setVenceEn(r.getFechaCreacionReserva().plusMinutes(reservaMinutos));
        reservas.save(r); evaluarAlerta(s); stocks.flush(); return reservaRespuesta(r);
    }
    public Map<String,Object> consultarReserva(Long id) {
        ReservaStock r = buscarReserva(id); expirar(r.getStock()); return reservaRespuesta(r);
    }
    public Map<String,Object> confirmar(Long id) {
        ReservaStock r = buscarReserva(id); Stock s = r.getStock(); expirar(s);
        if (r.getEstadoReserva() == EstadoReserva.CONFIRMADA) return reservaRespuesta(r);
        // Return expired state without rolling back expiration changes.
        if (r.getEstadoReserva() == EstadoReserva.EXPIRADA) return reservaRespuesta(r);
        if (r.getEstadoReserva() != EstadoReserva.ACTIVA) throw new IllegalStateException("La reserva no está activa");
        int anterior = s.getCantidadFisica();
        s.setCantidadFisica(anterior-r.getCantidadReserva());
        s.setCantidadReservada(s.getCantidadReservada()-r.getCantidadReserva());
        r.setEstadoReserva(EstadoReserva.CONFIRMADA);
        movimiento(s,TipoMovimiento.VENTA,r.getCantidadReserva(),anterior,"venta-reserva:"+r.getIdReservaStock());
        evaluarAlerta(s); stocks.flush(); return reservaRespuesta(r);
    }
    public Map<String,Object> liberar(Long id) {
        ReservaStock r = buscarReserva(id); Stock s = r.getStock(); expirar(s);
        if (r.getEstadoReserva() == EstadoReserva.LIBERADA || r.getEstadoReserva() == EstadoReserva.EXPIRADA)
            return reservaRespuesta(r);
        if (r.getEstadoReserva() != EstadoReserva.ACTIVA) throw new IllegalStateException("No puedes liberar una venta confirmada");
        s.setCantidadReservada(s.getCantidadReservada()-r.getCantidadReserva()); r.setEstadoReserva(EstadoReserva.LIBERADA);
        evaluarAlerta(s); stocks.flush(); return reservaRespuesta(r);
    }
    public List<Map<String,Object>> listarMovimientos(Long idProducto) {
        Stock s = buscarStock(idProducto);
        return movimientos.findAll().stream().filter(m -> m.getStock().getIdStock().equals(s.getIdStock()))
            .sorted(Comparator.comparing(MovimientoStock::getFecha).reversed()).map(m -> {
                Map<String,Object> dato = new LinkedHashMap<>();
                dato.put("idMovimientoStock",m.getIdMovimientoStock()); dato.put("idProducto",idProducto);
                dato.put("tipo",m.getTipo()); dato.put("cantidadMovimiento",m.getCantidadMovimiento());
                dato.put("stockAnterior",m.getStockAnterior()); dato.put("stockPosterior",m.getStockPosterior());
                dato.put("fecha",m.getFecha()); dato.put("referenciaOperacion",m.getReferenciaOperacion()); return dato;
            }).toList();
    }
    public List<Map<String,Object>> listarAlertas(boolean soloActivas) {
        stocks.findAll().forEach(this::expirar);
        return alertas.findAll().stream().filter(a -> !soloActivas || a.getEstadoAlerta() == EstadoAlerta.ACTIVA).map(a -> {
            Map<String,Object> dato = new LinkedHashMap<>();
            dato.put("idAlertaStock",a.getIdAlertaStock()); dato.put("idProducto",a.getStock().getIdProducto());
            dato.put("estadoAlerta",a.getEstadoAlerta()); dato.put("cantidadDetectada",a.getCantidadDetectada());
            dato.put("fechaCreacionAlerta",a.getFechaCreacionAlerta()); dato.put("resueltaEn",a.getResueltaEn()); return dato;
        }).toList();
    }
    public void expirarReservas() { stocks.findAll().forEach(this::expirar); }
}
