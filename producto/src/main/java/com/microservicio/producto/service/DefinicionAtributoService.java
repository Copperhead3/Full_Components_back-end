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
public class DefinicionAtributoService {
    @Autowired private DefinicionAtributoRepository repository;
    @Autowired private CategoriaService categoriaService;
    @Autowired private EspecificacionProductoRepository especificacionRepository;

    public List<DefinicionAtributo> listar() { return repository.findAll(); }
    public DefinicionAtributo buscar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser mayor que cero");
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Definición no encontrada: " + id));
    }
    private Categoria validar(DefinicionAtributo datos, Long excluir) {
        Categoria categoria = categoriaService.buscar(datos.getCategoria().getIdCategoria());
        String clave = datos.getClave().trim().toLowerCase(Locale.ROOT);
        if (repository.findAll().stream().anyMatch(a -> !Objects.equals(a.getIdDefinicionAtributo(), excluir)
            && a.getCategoria().getIdCategoria().equals(categoria.getIdCategoria()) && a.getClave().equalsIgnoreCase(clave)))
            throw new IllegalStateException("La clave ya existe en esta categoría");
        datos.setClave(clave);
        return categoria;
    }
    public DefinicionAtributo crear(DefinicionAtributo datos) {
        if (datos.getIdDefinicionAtributo() != null) throw new IllegalArgumentException("No envíes ID al crear");
        datos.setCategoria(validar(datos, null));
        return repository.save(datos);
    }
    public DefinicionAtributo editar(Long id, DefinicionAtributo datos) {
        DefinicionAtributo actual = buscar(id);
        Categoria categoria = validar(datos, id);
        boolean usada = especificacionRepository.findAll().stream()
            .anyMatch(e -> e.getAtributo().getIdDefinicionAtributo().equals(id));
        if (usada && (!actual.getCategoria().getIdCategoria().equals(categoria.getIdCategoria())
            || !actual.getTipoDato().equals(datos.getTipoDato())))
            throw new IllegalStateException("No puedes cambiar categoría o tipo de una definición utilizada");
        actual.setCategoria(categoria); actual.setClave(datos.getClave());
        actual.setNombreDefinicionAtributo(datos.getNombreDefinicionAtributo().trim());
        actual.setTipoDato(datos.getTipoDato()); actual.setUnidad(datos.getUnidad()); actual.setFiltrable(datos.isFiltrable());
        return repository.save(actual);
    }
    public void eliminar(Long id) {
        DefinicionAtributo actual = buscar(id);
        if (especificacionRepository.findAll().stream().anyMatch(e -> e.getAtributo().getIdDefinicionAtributo().equals(id)))
            throw new IllegalStateException("La definición está utilizada en especificaciones");
        actual.getCategoria().getAtributos().remove(actual);
        repository.delete(actual);
    }
}
