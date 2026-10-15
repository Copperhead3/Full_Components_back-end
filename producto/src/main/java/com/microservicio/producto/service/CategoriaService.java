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
public class CategoriaService {
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private EspecificacionProductoRepository especificacionRepository;

    public List<Categoria> listar() { return categoriaRepository.findAll(); }
    public Categoria buscar(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
        return categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada: " + idCategoria));
    }
    private void validarNombre(String nombre, Long excluir) {
        boolean duplicado = categoriaRepository.findAll().stream().anyMatch(c ->
            !Objects.equals(c.getIdCategoria(), excluir) && c.getNombreCategoria().equalsIgnoreCase(nombre.trim()));
        if (duplicado) throw new IllegalStateException("Ya existe una categoría con ese nombre");
    }
    public Categoria crear(Categoria datos) {
        if (datos.getIdCategoria() != null) throw new IllegalArgumentException("No envíes ID al crear una categoría");
        validarNombre(datos.getNombreCategoria(), null);
        datos.setNombreCategoria(datos.getNombreCategoria().trim());
        datos.setAtributos(new ArrayList<>());
        return categoriaRepository.save(datos);
    }
    public Categoria editar(Long id, Categoria datos) {
        Categoria actual = buscar(id);
        validarNombre(datos.getNombreCategoria(), id);
        actual.setNombreCategoria(datos.getNombreCategoria().trim());
        return categoriaRepository.save(actual);
    }
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productoRepository.findAll().stream().anyMatch(p -> p.getCategoria().getIdCategoria().equals(id)))
            throw new IllegalStateException("No puedes eliminar una categoría con productos");
        if (especificacionRepository.findAll().stream().anyMatch(e -> e.getAtributo().getCategoria().getIdCategoria().equals(id)))
            throw new IllegalStateException("Hay especificaciones que usan atributos de la categoría");
        categoriaRepository.delete(categoria);
    }
}
