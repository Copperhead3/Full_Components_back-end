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
public class ImagenProductoService {
    @Autowired private ImagenProductoRepository repository;
    @Autowired private ProductoService productoService;

    public List<ImagenProducto> listar() { return repository.findAll(); }
    public ImagenProducto buscar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Imagen no encontrada: " + id));
    }
    private void validarPrincipal(Producto producto, boolean principal, Long excluir) {
        if (principal && repository.findAll().stream().anyMatch(i -> !Objects.equals(i.getIdImagenProducto(), excluir)
            && i.getProducto().getIdProducto().equals(producto.getIdProducto()) && i.isEsPrincipal()))
            throw new IllegalStateException("El producto ya tiene una imagen principal; desmarca la anterior primero");
    }
    public ImagenProducto crear(ImagenProducto datos) {
        if (datos.getIdImagenProducto() != null) throw new IllegalArgumentException("No envíes ID al crear una imagen");
        Producto producto = productoService.buscar(datos.getProducto().getIdProducto());
        validarPrincipal(producto, datos.isEsPrincipal(), null);
        datos.setProducto(producto); datos.setUrl(datos.getUrl().trim());
        producto.getImagenes().add(datos);
        return repository.save(datos);
    }
    public ImagenProducto editar(Long id, ImagenProducto datos) {
        ImagenProducto actual = buscar(id);
        if (!actual.getProducto().getIdProducto().equals(datos.getProducto().getIdProducto()))
            throw new IllegalArgumentException("No puedes trasladar la imagen a otro producto");
        validarPrincipal(actual.getProducto(), datos.isEsPrincipal(), id);
        if (actual.isEsPrincipal() && !datos.isEsPrincipal()
            && actual.getProducto().getEstado() == EstadoPublicacion.PUBLICADO)
            throw new IllegalStateException("Despublica el producto antes de desmarcar su imagen principal");
        actual.setUrl(datos.getUrl().trim()); actual.setFormato(datos.getFormato());
        actual.setOrden(datos.getOrden()); actual.setEsPrincipal(datos.isEsPrincipal());
        return repository.save(actual);
    }
    public void eliminar(Long id) {
        ImagenProducto actual = buscar(id);
        if (actual.isEsPrincipal() && actual.getProducto().getEstado() == EstadoPublicacion.PUBLICADO)
            throw new IllegalStateException("Despublica el producto antes de eliminar su imagen principal");
        actual.getProducto().getImagenes().remove(actual);
        repository.delete(actual);
    }
}
