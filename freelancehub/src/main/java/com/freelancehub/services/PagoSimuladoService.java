package com.freelancehub.services;

import com.freelancehub.controllers.dtos.responses.PagoSimuladoResponse;
import com.freelancehub.entities.Cotizacion;
import com.freelancehub.entities.PagoSimulado;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.enums.EstadoCotizacion;
import com.freelancehub.enums.EstadoEvidencia;
import com.freelancehub.enums.EstadoPagoSimulado;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.repositories.ArbitrajeRepository;
import com.freelancehub.repositories.CotizacionRepository;
import com.freelancehub.repositories.EntregaEvidenciaRepository;
import com.freelancehub.repositories.PagoSimuladoRepository;
import com.freelancehub.repositories.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagoSimuladoService {

    private static final String NOTA_SIMULACION =
            "Este pago es simulado para fines académicos. No representa una transacción real.";

    private final PagoSimuladoRepository pagoRepository;
    private final ProyectoRepository proyectoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final EntregaEvidenciaRepository evidenciaRepository;
    private final ArbitrajeRepository arbitrajeRepository;

    public PagoSimuladoService(PagoSimuladoRepository pagoRepository,
                               ProyectoRepository proyectoRepository,
                               CotizacionRepository cotizacionRepository,
                               EntregaEvidenciaRepository evidenciaRepository,
                               ArbitrajeRepository arbitrajeRepository) {
        this.pagoRepository = pagoRepository;
        this.proyectoRepository = proyectoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.arbitrajeRepository = arbitrajeRepository;
    }

    @Transactional
    public PagoSimuladoResponse createForProject(Long projectId, Long clienteId) {
        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        if (!proyecto.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede crear el pago simulado del proyecto");
        }

        if (proyecto.getEstado() != EstadoProyecto.EN_CONTRATO
                && proyecto.getEstado() != EstadoProyecto.EN_PROCESO
                && proyecto.getEstado() != EstadoProyecto.EN_REVISION) {
            throw new BusinessException("Solo se puede crear pago simulado para proyectos contratados");
        }

        if (pagoRepository.findByProyectoId(projectId).isPresent()) {
            throw new BusinessException("Este proyecto ya tiene un pago simulado");
        }

        Cotizacion cotizacionAceptada = cotizacionRepository
                .findByProyectoIdAndEstado(projectId, EstadoCotizacion.ACEPTADA)
                .orElseThrow(() -> new BusinessException(
                        "No existe una cotización aceptada para crear el pago simulado"));

        PagoSimulado pago = new PagoSimulado();
        pago.setProyecto(proyecto);
        pago.setCotizacion(cotizacionAceptada);
        pago.setCliente(proyecto.getCliente());
        pago.setProfesional(cotizacionAceptada.getProfesional());
        pago.setMonto(cotizacionAceptada.getPrecio());
        pago.setEstado(EstadoPagoSimulado.RETENIDO);
        pago.setReferencia(generateReference());
        pago.setDescripcion("Pago simulado retenido para el proyecto " + proyecto.getTitulo());

        return toResponse(pagoRepository.save(pago));
    }

    @Transactional(readOnly = true)
    public PagoSimuladoResponse getForProject(Long projectId, Long currentUserId, RolUsuario currentRol) {
        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        PagoSimulado pago = pagoRepository.findByProyectoId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Este proyecto todavía no tiene pago simulado"));

        validateCanView(proyecto, pago, currentUserId, currentRol);
        return toResponse(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoSimuladoResponse> listMine(Long currentUserId, RolUsuario currentRol) {
        return switch (currentRol) {
            case CLIENTE -> pagoRepository.findByClienteIdOrderByFechaCreacionDesc(currentUserId)
                    .stream().map(this::toResponse).toList();
            case PROFESIONAL -> pagoRepository.findByProfesionalIdOrderByFechaCreacionDesc(currentUserId)
                    .stream().map(this::toResponse).toList();
            case ADMIN -> pagoRepository.findAll()
                    .stream().map(this::toResponse).toList();
        };
    }

    @Transactional(readOnly = true)
    public List<PagoSimuladoResponse> listAll() {
        return pagoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PagoSimuladoResponse release(Long paymentId, Long clienteId) {
        PagoSimulado pago = pagoRepository.findWithRelationsById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago simulado no encontrado con id: " + paymentId));

        if (!pago.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede liberar este pago simulado");
        }

        if (pago.getEstado() == EstadoPagoSimulado.LIBERADO) {
            throw new BusinessException("Este pago simulado ya fue liberado");
        }
        if (pago.getEstado() == EstadoPagoSimulado.DEVUELTO) {
            throw new BusinessException("Este pago simulado ya fue devuelto al cliente por arbitraje");
        }
        if (pago.getEstado() != EstadoPagoSimulado.RETENIDO) {
            throw new BusinessException("Solo se pueden liberar pagos en estado RETENIDO");
        }

        boolean tieneArbitrajeActivo = arbitrajeRepository.existsByProyectoIdAndEstadoIn(
                pago.getProyecto().getId(),
                java.util.Set.of(
                        com.freelancehub.enums.EstadoArbitraje.ABIERTO,
                        com.freelancehub.enums.EstadoArbitraje.RESPONDIDO,
                        com.freelancehub.enums.EstadoArbitraje.EN_REVISION,
                        com.freelancehub.enums.EstadoArbitraje.CORRECCION_SOLICITADA
                )
        );
        if (tieneArbitrajeActivo) {
            throw new BusinessException("No puedes liberar el pago porque existe un arbitraje en proceso");
        }

        boolean tieneEvidenciaAprobada = evidenciaRepository.existsByProyectoIdAndEstado(
                pago.getProyecto().getId(), EstadoEvidencia.APROBADA);

        if (!tieneEvidenciaAprobada) {
            throw new BusinessException("Debes aprobar una evidencia antes de liberar el pago");
        }

        pago.setEstado(EstadoPagoSimulado.LIBERADO);
        pago.setFechaLiberacion(LocalDateTime.now());
        pago.setDescripcion("Pago simulado liberado al profesional después de aprobar la evidencia de entrega");

        Proyecto proyecto = pago.getProyecto();
        if (proyecto.getEstado() != EstadoProyecto.CERRADO) {
            proyecto.setEstado(EstadoProyecto.CERRADO);
            proyectoRepository.save(proyecto);
        }

        return toResponse(pagoRepository.save(pago));
    }

    private void validateCanView(Proyecto proyecto, PagoSimulado pago, Long currentUserId, RolUsuario currentRol) {
        switch (currentRol) {
            case CLIENTE -> {
                if (!proyecto.getCliente().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para ver el pago de este proyecto");
                }
            }
            case PROFESIONAL -> {
                if (!pago.getProfesional().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para ver el pago de este proyecto");
                }
            }
            case ADMIN -> {
                // ADMIN puede ver todos los pagos simulados.
            }
        }
    }

    private String generateReference() {
        return "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PagoSimuladoResponse toResponse(PagoSimulado pago) {
        PagoSimuladoResponse r = new PagoSimuladoResponse();
        r.setId(pago.getId());
        r.setProyectoId(pago.getProyecto().getId());
        r.setProyectoTitulo(pago.getProyecto().getTitulo());
        r.setCotizacionId(pago.getCotizacion().getId());
        r.setClienteId(pago.getCliente().getId());
        r.setClienteNombre(pago.getCliente().getNombre());
        r.setProfesionalId(pago.getProfesional().getId());
        r.setProfesionalNombre(pago.getProfesional().getNombre());
        r.setMonto(pago.getMonto());
        r.setEstado(pago.getEstado());
        r.setFechaCreacion(pago.getFechaCreacion());
        r.setFechaLiberacion(pago.getFechaLiberacion());
        r.setReferencia(pago.getReferencia());
        r.setDescripcion(pago.getDescripcion());
        r.setNotaSimulacion(NOTA_SIMULACION);
        return r;
    }
}
