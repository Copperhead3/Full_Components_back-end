package com.microservicio.producto.service;

import java.util.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservicio.producto.model.*;
import com.microservicio.producto.repository.*;
import com.microservicio.producto.dto.StockDTO;

@Service
@Transactional
public class ProductoService {
    @Autowired private ProductoRepository repository;
    @Autowired private CategoriaService categoriaService;
    @Autowired private InventarioClientService inventarioClientService;

    public List<Producto> listar() { return repository.findAll(); }
    public Producto buscar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
    }
    public Producto buscarPublicado(Long id) {
        Producto producto = buscar(id);
        if (producto.getEstado() != EstadoPublicacion.PUBLICADO)
            throw new NoSuchElementException("Producto no disponible en el catálogo");
        return producto;
    }
    public Producto crear(Producto datos) {
        if (datos.getIdProducto() != null) throw new IllegalArgumentException("No envíes ID al crear un producto");
        datos.setCategoria(categoriaService.buscar(datos.getCategoria().getIdCategoria()));
        datos.setEstado(EstadoPublicacion.BORRADOR);
        datos.setImagenes(new ArrayList<>()); datos.setEspecificaciones(new ArrayList<>()); datos.setRelacionados(new ArrayList<>());
        return repository.save(datos);
    }
    public Producto editar(Long id, Producto datos) {
        Producto actual = buscar(id);
        Categoria categoria = categoriaService.buscar(datos.getCategoria().getIdCategoria());
        if (!actual.getCategoria().getIdCategoria().equals(categoria.getIdCategoria()) && !actual.getEspecificaciones().isEmpty())
            throw new IllegalStateException("Elimina las especificaciones anteriores antes de cambiar la categoría");
        actual.setNombreProducto(datos.getNombreProducto().trim()); actual.setMarca(datos.getMarca().trim());
        actual.setModelo(datos.getModelo().trim()); actual.setDescripcion(datos.getDescripcion().trim());
        actual.setPrecioVigente(datos.getPrecioVigente()); actual.setCategoria(categoria);
        actual.setGarantia(datos.getGarantia()); actual.setDespacho(datos.getDespacho());
        return repository.save(actual);
    }
    public Producto publicar(Long id) {
        Producto actual = buscar(id);
        if (actual.getImagenes().stream().filter(ImagenProducto::isEsPrincipal).count() != 1)
            throw new IllegalStateException("Debe existir exactamente una imagen principal antes de publicar");
        if (actual.getEspecificaciones().isEmpty())
            throw new IllegalStateException("Agrega las especificaciones del producto antes de publicar");
        actual.setEstado(EstadoPublicacion.PUBLICADO);
        return repository.save(actual);
    }
    public Producto despublicar(Long id) {
        Producto actual = buscar(id); actual.setEstado(EstadoPublicacion.DESPUBLICADO);
        return repository.save(actual);
    }
    public Producto actualizarRelacionados(Long id, List<Long> ids) {
        if (ids == null) throw new IllegalArgumentException("Envía una lista de IDs");
        Producto actual = buscar(id);
        List<Producto> relacionados = new ArrayList<>();
        for (Long relacionadoId : new LinkedHashSet<>(ids)) {
            Producto candidato = buscarPublicado(relacionadoId);
            if (id.equals(relacionadoId)) throw new IllegalArgumentException("Un producto no puede relacionarse consigo mismo");
            if (!actual.getCategoria().getIdCategoria().equals(candidato.getCategoria().getIdCategoria()))
                throw new IllegalArgumentException("Los productos relacionados deben pertenecer a la misma categoría");
            relacionados.add(candidato);
        }
        actual.getRelacionados().clear(); actual.getRelacionados().addAll(relacionados);
        return repository.save(actual);
    }
    public List<Producto> relacionados(Long id) {
        return buscarPublicado(id).getRelacionados().stream()
            .filter(p -> p.getEstado() == EstadoPublicacion.PUBLICADO)
            .filter(p -> p.getCategoria().getIdCategoria().equals(buscar(id).getCategoria().getIdCategoria())).toList();
    }
    public List<Producto> catalogo(String texto, Long idCategoria, BigDecimal minimo,
            BigDecimal maximo, String orden, Map<String, String> tecnicos) {
        if (minimo != null && minimo.signum() < 0 || maximo != null && maximo.signum() < 0)
            throw new IllegalArgumentException("Los precios no pueden ser negativos");
        if (minimo != null && maximo != null && minimo.compareTo(maximo) > 0)
            throw new IllegalArgumentException("El mínimo no puede superar el máximo");
        if (!Set.of("nombre", "precio-asc", "precio-desc").contains(orden))
            throw new IllegalArgumentException("Orden válido: nombre, precio-asc o precio-desc");
        if (idCategoria != null) categoriaService.buscar(idCategoria);
        Map<String, String> filtros = new LinkedHashMap<>();
        tecnicos.forEach((k,v) -> { if (k.startsWith("atributo.")) filtros.put(k.substring(9),v); });
        if (!filtros.isEmpty()) {
            if (idCategoria == null) throw new IllegalArgumentException("Selecciona una categoría para filtrar características");
            Categoria categoria = categoriaService.buscar(idCategoria);
            for (String clave : filtros.keySet()) {
                if (categoria.getAtributos().stream().noneMatch(a -> a.isFiltrable() && a.getClave().equals(clave)))
                    throw new IllegalArgumentException("Filtro técnico inválido para esta categoría: " + clave);
            }
        }
        String busqueda = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        Comparator<Producto> comparador = Comparator.comparing(Producto::getNombreProducto, String.CASE_INSENSITIVE_ORDER);
        if (orden.equals("precio-asc")) comparador = Comparator.comparing(Producto::getPrecioVigente);
        if (orden.equals("precio-desc")) comparador = Comparator.comparing(Producto::getPrecioVigente).reversed();
        return repository.findAll().stream().filter(p -> p.getEstado() == EstadoPublicacion.PUBLICADO)
            .filter(p -> (p.getNombreProducto()+" "+p.getMarca()+" "+p.getModelo()).toLowerCase(Locale.ROOT).contains(busqueda))
            .filter(p -> idCategoria == null || p.getCategoria().getIdCategoria().equals(idCategoria))
            .filter(p -> minimo == null || p.getPrecioVigente().compareTo(minimo) >= 0)
            .filter(p -> maximo == null || p.getPrecioVigente().compareTo(maximo) <= 0)
            .filter(p -> filtros.entrySet().stream().allMatch(f -> p.getEspecificaciones().stream()
                .anyMatch(e -> e.getAtributo().getClave().equals(f.getKey()) && e.getValor().equalsIgnoreCase(f.getValue()))))
            .sorted(comparador).toList();
    }

    public Map<String,Object> buscarPublicadoConStock(Long idProducto) {
        Producto producto = buscarPublicado(idProducto);
        StockDTO stock = inventarioClientService.consultarStock(idProducto);
        // Build a flat response inside the transaction, without serializing JPA proxies.
        Map<String,Object> ficha = new LinkedHashMap<>();
        ficha.put("idProducto",producto.getIdProducto());
        ficha.put("nombreProducto",producto.getNombreProducto());
        ficha.put("marca",producto.getMarca());
        ficha.put("modelo",producto.getModelo());
        ficha.put("descripcion",producto.getDescripcion());
        ficha.put("precioVigente",producto.getPrecioVigente());
        ficha.put("estado",producto.getEstado());
        Map<String,Object> categoria = new LinkedHashMap<>();
        categoria.put("idCategoria",producto.getCategoria().getIdCategoria());
        categoria.put("nombreCategoria",producto.getCategoria().getNombreCategoria());
        ficha.put("categoria",categoria);
        ficha.put("imagenes",producto.getImagenes().stream()
            .sorted(Comparator.comparing(ImagenProducto::getOrden)).map(imagen -> {
                Map<String,Object> dato = new LinkedHashMap<>();
                dato.put("idImagenProducto",imagen.getIdImagenProducto());
                dato.put("url",imagen.getUrl()); dato.put("formato",imagen.getFormato());
                dato.put("orden",imagen.getOrden()); dato.put("esPrincipal",imagen.isEsPrincipal());
                return dato;
            }).toList());
        ficha.put("especificaciones",producto.getEspecificaciones().stream().map(especificacion -> {
            Map<String,Object> dato = new LinkedHashMap<>();
            dato.put("idEspecificacionProducto",especificacion.getIdEspecificacionProducto());
            dato.put("valor",especificacion.getValor());
            DefinicionAtributo definicion = especificacion.getAtributo();
            Map<String,Object> atributo = new LinkedHashMap<>();
            atributo.put("idDefinicionAtributo",definicion.getIdDefinicionAtributo());
            atributo.put("clave",definicion.getClave());
            atributo.put("nombreDefinicionAtributo",definicion.getNombreDefinicionAtributo());
            atributo.put("tipoDato",definicion.getTipoDato()); atributo.put("unidad",definicion.getUnidad());
            atributo.put("filtrable",definicion.isFiltrable());
            dato.put("atributo",atributo);
            return dato;
        }).toList());
        ficha.put("garantia",producto.getGarantia());
        ficha.put("despacho",producto.getDespacho());
        ficha.put("cantidadDisponible",stock.getCantidadDisponible());
        ficha.put("stockConsultado",stock.isStockConsultado());
        ficha.put("mensajeStock",stock.getMensajeStock());
        return ficha;
    }
}
