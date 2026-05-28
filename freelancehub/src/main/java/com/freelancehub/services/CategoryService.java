package com.freelancehub.services;

import com.freelancehub.controllers.dtos.request.CategoryRequest;
import com.freelancehub.controllers.dtos.responses.CategoryResponse;
import com.freelancehub.entities.Categoria;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.mapper.CategoriaMapper;
import com.freelancehub.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoryService(CategoriaRepository categoriaRepository,
                           CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoryResponse> listActive() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    public List<CategoryResponse> listAll() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + id));
        return categoriaMapper.toResponse(c);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoriaRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        Categoria c = categoriaMapper.toEntity(request);
        if (c.getActivo() == null) {
            c.setActivo(true);
        }
        c = categoriaRepository.save(c);
        return categoriaMapper.toResponse(c);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + id));

        if (request.getNombre() != null && !request.getNombre().equals(c.getNombre())) {
            categoriaRepository.findByNombre(request.getNombre())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new BusinessException(
                                "Ya existe otra categoría con el nombre: " + request.getNombre());
                    });
        }

        categoriaMapper.updateFromRequest(request, c);
        c = categoriaRepository.save(c);
        return categoriaMapper.toResponse(c);
    }

    @Transactional
    public void delete(Long id) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + id));
        // Soft-delete: marcar como inactiva en lugar de borrar físicamente,
        // para no romper proyectos existentes que referencian la categoría.
        c.setActivo(false);
        categoriaRepository.save(c);
    }
}
