package com.freelancehub.services;

import com.freelancehub.controllers.dtos.request.CreateQuoteRequest;
import com.freelancehub.controllers.dtos.responses.QuoteResponse;
import com.freelancehub.entities.Cotizacion;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.entities.Usuario;
import com.freelancehub.enums.EstadoCotizacion;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.mapper.CotizacionMapper;
import com.freelancehub.repositories.CotizacionRepository;
import com.freelancehub.repositories.ProyectoRepository;
import com.freelancehub.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuoteService {

    private final CotizacionRepository cotizacionRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionMapper cotizacionMapper;

    public QuoteService(CotizacionRepository cotizacionRepository,
                        ProyectoRepository proyectoRepository,
                        UsuarioRepository usuarioRepository,
                        CotizacionMapper cotizacionMapper) {
        this.cotizacionRepository = cotizacionRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionMapper = cotizacionMapper;
    }

    @Transactional
    public QuoteResponse createQuote(Long projectId, Long profesionalId, CreateQuoteRequest request) {
        Usuario profesional = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profesional no encontrado con id: " + profesionalId));

        if (profesional.getRol() != RolUsuario.PROFESIONAL) {
            throw new ForbiddenException("Solo los profesionales pueden enviar cotizaciones");
        }

        Proyecto proyecto = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        if (proyecto.getEstado() != EstadoProyecto.ABIERTO) {
            throw new BusinessException(
                    "Solo se pueden enviar cotizaciones a proyectos en estado ABIERTO");
        }

        if (cotizacionRepository.existsByProyectoIdAndProfesionalId(projectId, profesionalId)) {
            throw new BusinessException(
                    "Ya envió una cotización para este proyecto");
        }

        Cotizacion c = new Cotizacion();
        c.setProyecto(proyecto);
        c.setProfesional(profesional);
        c.setPrecio(request.getPrecio());
        c.setPlazo(request.getPlazo());
        c.setDescripcion(request.getDescripcion());
        c.setEstado(EstadoCotizacion.PENDIENTE);
        c = cotizacionRepository.save(c);

        return cotizacionMapper.toResponse(c);
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> listForProject(Long projectId, Long currentClienteId) {
        Proyecto proyecto = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        if (!proyecto.getCliente().getId().equals(currentClienteId)) {
            throw new ForbiddenException(
                    "Solo el cliente dueño del proyecto puede ver sus cotizaciones");
        }

        return cotizacionRepository.findByProyectoId(projectId).stream()
                .map(cotizacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> listMyQuotes(Long profesionalId) {
        return cotizacionRepository.findByProfesionalId(profesionalId).stream()
                .map(cotizacionMapper::toResponse)
                .toList();
    }

    /**
     * El cliente dueño del proyecto acepta una cotización PENDIENTE.
     *
     * <p>Efectos transaccionales:
     * <ul>
     *   <li>La cotización aceptada pasa a estado {@link EstadoCotizacion#ACEPTADA}.</li>
     *   <li>El proyecto pasa a estado {@link EstadoProyecto#EN_CONTRATO} (deja de estar abierto).</li>
     *   <li>Las demás cotizaciones PENDIENTES del mismo proyecto pasan a {@link EstadoCotizacion#RECHAZADA},
     *       porque ya hay una elegida.</li>
     * </ul>
     *
     * <p>Validaciones:
     * <ul>
     *   <li>El proyecto debe existir.</li>
     *   <li>El usuario actual debe ser el cliente dueño del proyecto.</li>
     *   <li>La cotización debe existir y pertenecer al proyecto.</li>
     *   <li>El proyecto debe estar ABIERTO (no se puede aceptar otra cotización si ya hay contrato).</li>
     *   <li>La cotización debe estar en estado PENDIENTE.</li>
     * </ul>
     */
    @Transactional
    public QuoteResponse acceptQuote(Long projectId, Long quoteId, Long currentClienteId) {
        Proyecto proyecto = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado con id: " + projectId));

        if (!proyecto.getCliente().getId().equals(currentClienteId)) {
            throw new ForbiddenException(
                    "Solo el cliente dueño del proyecto puede aceptar cotizaciones");
        }

        if (proyecto.getEstado() != EstadoProyecto.ABIERTO) {
            throw new BusinessException(
                    "Solo se pueden aceptar cotizaciones en proyectos en estado ABIERTO");
        }

        Cotizacion cotizacion = cotizacionRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cotización no encontrada con id: " + quoteId));

        if (!cotizacion.getProyecto().getId().equals(projectId)) {
            throw new BusinessException(
                    "La cotización no pertenece al proyecto indicado");
        }

        if (cotizacion.getEstado() != EstadoCotizacion.PENDIENTE) {
            throw new BusinessException(
                    "Solo se pueden aceptar cotizaciones en estado PENDIENTE");
        }

        cotizacion.setEstado(EstadoCotizacion.ACEPTADA);
        proyecto.setEstado(EstadoProyecto.EN_CONTRATO);

        cotizacionRepository.findByProyectoId(projectId).stream()
                .filter(c -> !c.getId().equals(quoteId))
                .filter(c -> c.getEstado() == EstadoCotizacion.PENDIENTE)
                .forEach(c -> c.setEstado(EstadoCotizacion.RECHAZADA));

        Cotizacion saved = cotizacionRepository.save(cotizacion);
        proyectoRepository.save(proyecto);

        return cotizacionMapper.toResponse(saved);
    }
}