package com.microservicio.producto.service;

import java.util.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservicio.producto.model.*;
import com.microservicio.producto.repository.*;

@Service
@Transactional
public class EspecificacionProductoService {
    @Autowired private EspecificacionProductoRepository repository;
    @Autowired private ProductoService productoService;
    @Autowired private DefinicionAtributoService atributoService;

    public List<EspecificacionProducto> listar() { return repository.findAll(); }
    public EspecificacionProducto buscar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Especificación no encontrada: " + id));
    }
    private void validar(Producto producto, DefinicionAtributo atributo, String valor, Long excluir) {
        if (!producto.getCategoria().getIdCategoria().equals(atributo.getCategoria().getIdCategoria()))
            throw new IllegalArgumentException("El atributo no pertenece a la categoría del producto");
        if (repository.findAll().stream().anyMatch(e -> !Objects.equals(e.getIdEspecificacionProducto(), excluir)
            && e.getProducto().getIdProducto().equals(producto.getIdProducto())
            && e.getAtributo().getIdDefinicionAtributo().equals(atributo.getIdDefinicionAtributo())))
            throw new IllegalStateException("El producto ya tiene una especificación para ese atributo");
        if (atributo.getTipoDato().equals("NUMERO")) {
            try { new BigDecimal(valor.trim()); }
            catch (NumberFormatException ex) { throw new IllegalArgumentException("El atributo requiere un valor numérico"); }
        }
        if (atributo.getTipoDato().equals("BOOLEANO") && !valor.equalsIgnoreCase("true") && !valor.equalsIgnoreCase("false"))
            throw new IllegalArgumentException("El atributo requiere true o false");
    }
    public EspecificacionProducto crear(EspecificacionProducto datos) {
        if (datos.getIdEspecificacionProducto() != null) throw new IllegalArgumentException("No envíes ID al crear");
        Producto producto = productoService.buscar(datos.getProducto().getIdProducto());
        DefinicionAtributo atributo = atributoService.buscar(datos.getAtributo().getIdDefinicionAtributo());
        validar(producto, atributo, datos.getValor().trim(), null);
        datos.setProducto(producto); datos.setAtributo(atributo); datos.setValor(datos.getValor().trim());
        producto.getEspecificaciones().add(datos);
        return repository.save(datos);
    }
    public EspecificacionProducto editar(Long id, EspecificacionProducto datos) {
        EspecificacionProducto actual = buscar(id);
        if (!actual.getProducto().getIdProducto().equals(datos.getProducto().getIdProducto()))
            throw new IllegalArgumentException("No puedes trasladar la especificación a otro producto");
        DefinicionAtributo atributo = atributoService.buscar(datos.getAtributo().getIdDefinicionAtributo());
        validar(actual.getProducto(), atributo, datos.getValor().trim(), id);
        actual.setAtributo(atributo); actual.setValor(datos.getValor().trim());
        return repository.save(actual);
    }
    public void eliminar(Long id) {
        EspecificacionProducto actual = buscar(id);
        if (actual.getProducto().getEstado() == EstadoPublicacion.PUBLICADO
            && actual.getProducto().getEspecificaciones().size() <= 1)
            throw new IllegalStateException("Despublica el producto antes de eliminar su última especificación");
        actual.getProducto().getEspecificaciones().remove(actual);
        repository.delete(actual);
    }
}
