package com.freelancehub.services;

import com.freelancehub.controllers.dtos.request.CreateProjectRequest;
import com.freelancehub.controllers.dtos.responses.ProjectResponse;
import com.freelancehub.controllers.dtos.request.UpdateProjectRequest;
import com.freelancehub.entities.Categoria;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.entities.Usuario;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.mapper.ProyectoMapper;
import com.freelancehub.repositories.CategoriaRepository;
import com.freelancehub.repositories.ProyectoRepository;
import com.freelancehub.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProyectoMapper proyectoMapper;

    public ProjectService(ProyectoRepository proyectoRepository,
                          UsuarioRepository usuarioRepository,
                          CategoriaRepository categoriaRepository,
                          ProyectoMapper proyectoMapper) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.proyectoMapper = proyectoMapper;
    }

    @Transactional
    public ProjectResponse create(Long clienteId, CreateProjectRequest request) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + clienteId));

        if (cliente.getRol() != RolUsuario.CLIENTE) {
            throw new ForbiddenException("Solo los clientes pueden crear proyectos");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + request.getCategoriaId()));

        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ForbiddenException("La categoría seleccionada no está activa");
        }

        Proyecto p = new Proyecto();
        p.setCliente(cliente);
        p.setCategoria(categoria);
        p.setTitulo(request.getTitulo());
        p.setDescripcion(request.getDescripcion());
        p.setPresupuesto(request.getPresupuesto());
        p.setPlazo(request.getPlazo());
        p.setEstado(EstadoProyecto.ABIERTO);
        p = proyectoRepository.save(p);

        return proyectoMapper.toResponse(p);
    }

    @Transactional
    public ProjectResponse update(Long projectId, Long currentUserId, UpdateProjectRequest request) {
        Proyecto p = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        if (!p.getCliente().getId().equals(currentUserId)) {
            throw new ForbiddenException("Solo el cliente dueño puede editar este proyecto");
        }

        if (p.getEstado() != EstadoProyecto.ABIERTO) {
            throw new BusinessException(
                    "Solo se pueden editar proyectos en estado ABIERTO");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + request.getCategoriaId()));

        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ForbiddenException("La categoría seleccionada no está activa");
        }

        p.setCategoria(categoria);
        p.setTitulo(request.getTitulo());
        p.setDescripcion(request.getDescripcion());
        p.setPresupuesto(request.getPresupuesto());
        p.setPlazo(request.getPlazo());
        p = proyectoRepository.save(p);

        return proyectoMapper.toResponse(p);
    }

    @Transactional
    public void delete(Long projectId, Long currentUserId) {
        Proyecto p = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        if (!p.getCliente().getId().equals(currentUserId)) {
            throw new ForbiddenException("Solo el cliente dueño puede eliminar este proyecto");
        }

        if (p.getEstado() != EstadoProyecto.ABIERTO) {
            throw new BusinessException(
                    "Solo se pueden eliminar proyectos en estado ABIERTO");
        }

        proyectoRepository.delete(p);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listOpen(Long categoriaId, BigDecimal minBudget, BigDecimal maxBudget) {
        return proyectoRepository
                .findOpenProjectsWithFilters(EstadoProyecto.ABIERTO, categoriaId, minBudget, maxBudget)
                .stream()
                .map(proyectoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listMy(Long clienteId) {
        return proyectoRepository.findByClienteId(clienteId).stream()
                .map(proyectoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listAll() {
        return proyectoRepository.findAll().stream()
                .map(proyectoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getByIdForCurrentUser(Long projectId, Long currentUserId, RolUsuario currentRol) {
        Proyecto p = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        switch (currentRol) {
            case CLIENTE -> {
                if (!p.getCliente().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para ver este proyecto");
                }
            }
            case PROFESIONAL -> {
                if (p.getEstado() != EstadoProyecto.ABIERTO) {
                    throw new ForbiddenException(
                            "Solo puede ver proyectos en estado ABIERTO");
                }
            }
            case ADMIN -> {
                // ADMIN puede ver todos.
            }
        }
        return proyectoMapper.toResponse(p);
    }
}
