package com.freelancehub.services;

import com.freelancehub.controllers.dtos.request.ResolverArbitrajeRequest;
import com.freelancehub.controllers.dtos.responses.ArbitrajeResponse;
import com.freelancehub.entities.Arbitraje;
import com.freelancehub.entities.EntregaEvidencia;
import com.freelancehub.entities.PagoSimulado;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.enums.EstadoArbitraje;
import com.freelancehub.enums.EstadoEvidencia;
import com.freelancehub.enums.EstadoPagoSimulado;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.repositories.ArbitrajeRepository;
import com.freelancehub.repositories.EntregaEvidenciaRepository;
import com.freelancehub.repositories.PagoSimuladoRepository;
import com.freelancehub.repositories.ProyectoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ArbitrajeService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "png", "jpg", "jpeg", "docx");
    private static final Set<EstadoArbitraje> ESTADOS_ACTIVOS = Set.of(
            EstadoArbitraje.ABIERTO,
            EstadoArbitraje.RESPONDIDO,
            EstadoArbitraje.EN_REVISION,
            EstadoArbitraje.CORRECCION_SOLICITADA
    );

    private final ArbitrajeRepository arbitrajeRepository;
    private final EntregaEvidenciaRepository evidenciaRepository;
    private final PagoSimuladoRepository pagoRepository;
    private final ProyectoRepository proyectoRepository;
    private final Path uploadDir;

    public ArbitrajeService(ArbitrajeRepository arbitrajeRepository,
                            EntregaEvidenciaRepository evidenciaRepository,
                            PagoSimuladoRepository pagoRepository,
                            ProyectoRepository proyectoRepository,
                            @Value("${app.uploads.arbitrations-dir:uploads/arbitrations}") String uploadDir) {
        this.arbitrajeRepository = arbitrajeRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.pagoRepository = pagoRepository;
        this.proyectoRepository = proyectoRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public ArbitrajeResponse create(Long evidenceId, Long clienteId, String motivo, MultipartFile file) {
        String motivoLimpio = validarTexto(motivo, "El motivo del arbitraje", 10);
        validarArchivoOpcional(file);

        EntregaEvidencia evidencia = evidenciaRepository.findWithRelationsById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada con id: " + evidenceId));
        Proyecto proyecto = evidencia.getProyecto();

        if (!proyecto.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede iniciar arbitraje sobre esta evidencia");
        }
        if (evidencia.getEstado() == EstadoEvidencia.APROBADA) {
            throw new BusinessException("No puedes iniciar arbitraje sobre una evidencia ya aprobada");
        }
        if (arbitrajeRepository.existsByEvidenciaIdAndEstadoIn(evidenceId, ESTADOS_ACTIVOS)) {
            throw new BusinessException("Ya existe un arbitraje activo para esta evidencia");
        }

        PagoSimulado pago = pagoRepository.findByProyectoId(proyecto.getId())
                .orElseThrow(() -> new BusinessException("El proyecto no tiene pago simulado retenido"));
        if (pago.getEstado() != EstadoPagoSimulado.RETENIDO) {
            throw new BusinessException("Solo se puede iniciar arbitraje cuando el pago está RETENIDO");
        }

        StoredFile stored = storeOptional(file, "cliente");

        Arbitraje arbitraje = new Arbitraje();
        arbitraje.setProyecto(proyecto);
        arbitraje.setEvidencia(evidencia);
        arbitraje.setPagoSimulado(pago);
        arbitraje.setCliente(proyecto.getCliente());
        arbitraje.setProfesional(evidencia.getProfesional());
        arbitraje.setMotivoCliente(motivoLimpio);
        arbitraje.setEstado(EstadoArbitraje.ABIERTO);
        if (stored != null) {
            arbitraje.setArchivoClienteNombre(stored.originalName());
            arbitraje.setArchivoClienteAlmacenado(stored.storedName());
            arbitraje.setArchivoClienteTipo(stored.contentType());
            arbitraje.setArchivoClienteRuta(stored.path().toString());
        }

        if (proyecto.getEstado() != EstadoProyecto.EN_DISPUTA) {
            proyecto.setEstado(EstadoProyecto.EN_DISPUTA);
            proyectoRepository.save(proyecto);
        }

        return toResponse(arbitrajeRepository.save(arbitraje));
    }

    @Transactional(readOnly = true)
    public List<ArbitrajeResponse> listMineAsClient(Long clienteId) {
        return arbitrajeRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArbitrajeResponse> listMineAsProfessional(Long profesionalId) {
        return arbitrajeRepository.findByProfesionalIdOrderByFechaCreacionDesc(profesionalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArbitrajeResponse> listAll() {
        return arbitrajeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArbitrajeResponse getForAdmin(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ArbitrajeResponse respond(Long id, Long profesionalId, String respuesta, MultipartFile file) {
        String respuestaLimpia = validarTexto(respuesta, "La respuesta del profesional", 10);
        validarArchivoOpcional(file);

        Arbitraje arbitraje = findById(id);
        if (!arbitraje.getProfesional().getId().equals(profesionalId)) {
            throw new ForbiddenException("Solo el profesional involucrado puede responder este arbitraje");
        }
        if (arbitraje.getEstado() != EstadoArbitraje.ABIERTO
                && arbitraje.getEstado() != EstadoArbitraje.EN_REVISION
                && arbitraje.getEstado() != EstadoArbitraje.CORRECCION_SOLICITADA) {
            throw new BusinessException("Este arbitraje no está disponible para responder");
        }

        StoredFile stored = storeOptional(file, "profesional");
        arbitraje.setRespuestaProfesional(respuestaLimpia);
        arbitraje.setFechaRespuestaProfesional(LocalDateTime.now());
        arbitraje.setEstado(EstadoArbitraje.RESPONDIDO);
        if (stored != null) {
            arbitraje.setArchivoProfesionalNombre(stored.originalName());
            arbitraje.setArchivoProfesionalAlmacenado(stored.storedName());
            arbitraje.setArchivoProfesionalTipo(stored.contentType());
            arbitraje.setArchivoProfesionalRuta(stored.path().toString());
        }
        return toResponse(arbitrajeRepository.save(arbitraje));
    }

    @Transactional
    public ArbitrajeResponse resolve(Long id, ResolverArbitrajeRequest request) {
        Arbitraje arbitraje = findById(id);
        String decision = validarTexto(request.getDecision(), "La decisión", 3).toUpperCase(Locale.ROOT).trim();
        String observacion = validarTexto(request.getObservacion(), "La observación", 5);

        Proyecto proyecto = arbitraje.getProyecto();
        PagoSimulado pago = arbitraje.getPagoSimulado();

        switch (decision) {
            case "A_FAVOR_CLIENTE" -> {
                arbitraje.setEstado(EstadoArbitraje.RESUELTO_A_FAVOR_CLIENTE);
                arbitraje.getEvidencia().setEstado(EstadoEvidencia.RECHAZADA);
                if (pago.getEstado() == EstadoPagoSimulado.RETENIDO) {
                    pago.setEstado(EstadoPagoSimulado.DEVUELTO);
                    pago.setFechaLiberacion(LocalDateTime.now());
                    pago.setDescripcion("Pago simulado devuelto al cliente por resolución de arbitraje a favor del cliente");
                    pagoRepository.save(pago);
                }
                proyecto.setEstado(EstadoProyecto.CANCELADO);
                proyectoRepository.save(proyecto);
                evidenciaRepository.save(arbitraje.getEvidencia());
            }
            case "A_FAVOR_PROFESIONAL" -> {
                arbitraje.setEstado(EstadoArbitraje.RESUELTO_A_FAVOR_PROFESIONAL);
                arbitraje.getEvidencia().setEstado(EstadoEvidencia.APROBADA);
                if (pago.getEstado() == EstadoPagoSimulado.RETENIDO) {
                    pago.setEstado(EstadoPagoSimulado.LIBERADO);
                    pago.setFechaLiberacion(LocalDateTime.now());
                    pago.setDescripcion("Pago simulado liberado al profesional por resolución de arbitraje a favor del profesional");
                    pagoRepository.save(pago);
                }
                proyecto.setEstado(EstadoProyecto.CERRADO);
                proyectoRepository.save(proyecto);
                evidenciaRepository.save(arbitraje.getEvidencia());
            }
            case "SOLICITAR_CORRECCION" -> {
                arbitraje.setEstado(EstadoArbitraje.CORRECCION_SOLICITADA);
                arbitraje.getEvidencia().setEstado(EstadoEvidencia.RECHAZADA);
                if (pago.getEstado() == EstadoPagoSimulado.RETENIDO) {
                    pago.setDescripcion("Pago simulado continúa retenido mientras el profesional realiza correcciones");
                    pagoRepository.save(pago);
                }
                proyecto.setEstado(EstadoProyecto.EN_REVISION);
                proyectoRepository.save(proyecto);
                evidenciaRepository.save(arbitraje.getEvidencia());
            }
            default -> throw new BusinessException("Decisión inválida. Use A_FAVOR_CLIENTE, A_FAVOR_PROFESIONAL o SOLICITAR_CORRECCION");
        }

        arbitraje.setDecisionAdmin(observacion);
        arbitraje.setFechaResolucion(LocalDateTime.now());
        return toResponse(arbitrajeRepository.save(arbitraje));
    }

    @Transactional(readOnly = true)
    public DownloadFile downloadClientFile(Long id, Long currentUserId, RolUsuario currentRol) {
        Arbitraje arbitraje = findAuthorized(id, currentUserId, currentRol);
        if (arbitraje.getArchivoClienteRuta() == null || arbitraje.getArchivoClienteRuta().isBlank()) {
            throw new ResourceNotFoundException("Este arbitraje no tiene archivo del cliente");
        }
        return prepareDownload(arbitraje.getArchivoClienteRuta(), arbitraje.getArchivoClienteNombre(), arbitraje.getArchivoClienteTipo());
    }

    @Transactional(readOnly = true)
    public DownloadFile downloadProfessionalFile(Long id, Long currentUserId, RolUsuario currentRol) {
        Arbitraje arbitraje = findAuthorized(id, currentUserId, currentRol);
        if (arbitraje.getArchivoProfesionalRuta() == null || arbitraje.getArchivoProfesionalRuta().isBlank()) {
            throw new ResourceNotFoundException("Este arbitraje no tiene archivo del profesional");
        }
        return prepareDownload(arbitraje.getArchivoProfesionalRuta(), arbitraje.getArchivoProfesionalNombre(), arbitraje.getArchivoProfesionalTipo());
    }

    public boolean existsActiveForProject(Long proyectoId) {
        return arbitrajeRepository.existsByProyectoIdAndEstadoIn(proyectoId, ESTADOS_ACTIVOS);
    }

    private Arbitraje findById(Long id) {
        return arbitrajeRepository.findWithRelationsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arbitraje no encontrado con id: " + id));
    }

    private Arbitraje findAuthorized(Long id, Long currentUserId, RolUsuario currentRol) {
        Arbitraje arbitraje = findById(id);
        switch (currentRol) {
            case CLIENTE -> {
                if (!arbitraje.getCliente().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para ver este arbitraje");
                }
            }
            case PROFESIONAL -> {
                if (!arbitraje.getProfesional().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para ver este arbitraje");
                }
            }
            case ADMIN -> {
                // ADMIN puede ver todos.
            }
        }
        return arbitraje;
    }

    private String validarTexto(String value, String field, int minLength) {
        if (value == null || value.trim().isBlank()) {
            throw new BusinessException(field + " es obligatorio");
        }
        String clean = value.trim();
        if (clean.length() < minLength) {
            throw new BusinessException(field + " debe tener al menos " + minLength + " caracteres");
        }
        return clean;
    }

    private void validarArchivoOpcional(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("El archivo no puede superar 10 MB");
        }
        String original = sanitizeFilename(file.getOriginalFilename());
        String ext = extensionOf(original);
        if (!EXTENSIONES_PERMITIDAS.contains(ext)) {
            throw new BusinessException("Formato no permitido. Use PDF, PNG, JPG, JPEG o DOCX");
        }
    }

    private StoredFile storeOptional(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Files.createDirectories(uploadDir);
            String original = sanitizeFilename(file.getOriginalFilename());
            String ext = extensionOf(original);
            String storedName = prefix + "-" + UUID.randomUUID() + "." + ext;
            Path target = uploadDir.resolve(storedName).normalize();
            if (!target.startsWith(uploadDir)) {
                throw new ForbiddenException("Ruta de archivo no permitida");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = Files.probeContentType(target);
            }
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }
            return new StoredFile(original, storedName, contentType, target);
        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el archivo del arbitraje");
        }
    }

    private DownloadFile prepareDownload(String ruta, String originalName, String contentType) {
        Path filePath = Paths.get(ruta).toAbsolutePath().normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new ForbiddenException("Ruta de archivo no permitida");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("El archivo físico del arbitraje no existe");
        }
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("No se puede leer el archivo del arbitraje");
            }
            return new DownloadFile(resource, originalName, contentType != null ? contentType : "application/octet-stream");
        } catch (MalformedURLException e) {
            throw new BusinessException("No se pudo preparar la descarga del archivo");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "soporte";
        }
        String justName = Paths.get(filename).getFileName().toString();
        return justName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String extensionOf(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private ArbitrajeResponse toResponse(Arbitraje arbitraje) {
        ArbitrajeResponse r = new ArbitrajeResponse();
        r.setId(arbitraje.getId());
        r.setProyectoId(arbitraje.getProyecto().getId());
        r.setProyectoTitulo(arbitraje.getProyecto().getTitulo());
        r.setEvidenciaId(arbitraje.getEvidencia().getId());
        r.setPagoId(arbitraje.getPagoSimulado().getId());
        r.setClienteId(arbitraje.getCliente().getId());
        r.setClienteNombre(arbitraje.getCliente().getNombre());
        r.setProfesionalId(arbitraje.getProfesional().getId());
        r.setProfesionalNombre(arbitraje.getProfesional().getNombre());
        r.setMotivoCliente(arbitraje.getMotivoCliente());
        r.setArchivoClienteNombre(arbitraje.getArchivoClienteNombre());
        if (arbitraje.getArchivoClienteNombre() != null) {
            r.setUrlDescargaArchivoCliente("/api/v1/arbitrations/" + arbitraje.getId() + "/client-file/download");
        }
        r.setRespuestaProfesional(arbitraje.getRespuestaProfesional());
        r.setArchivoProfesionalNombre(arbitraje.getArchivoProfesionalNombre());
        if (arbitraje.getArchivoProfesionalNombre() != null) {
            r.setUrlDescargaArchivoProfesional("/api/v1/arbitrations/" + arbitraje.getId() + "/professional-file/download");
        }
        r.setDecisionAdmin(arbitraje.getDecisionAdmin());
        r.setEstado(arbitraje.getEstado());
        r.setFechaCreacion(arbitraje.getFechaCreacion());
        r.setFechaRespuestaProfesional(arbitraje.getFechaRespuestaProfesional());
        r.setFechaResolucion(arbitraje.getFechaResolucion());
        return r;
    }

    private record StoredFile(String originalName, String storedName, String contentType, Path path) {}

    public record DownloadFile(Resource resource, String filename, String contentType) {}
}
