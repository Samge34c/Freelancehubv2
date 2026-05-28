package com.freelancehub.services;

import com.freelancehub.controllers.dtos.responses.EvidenciaResponse;
import com.freelancehub.entities.EntregaEvidencia;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.entities.Usuario;
import com.freelancehub.enums.EstadoCotizacion;
import com.freelancehub.enums.EstadoEvidencia;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.repositories.CotizacionRepository;
import com.freelancehub.repositories.EntregaEvidenciaRepository;
import com.freelancehub.repositories.ProyectoRepository;
import com.freelancehub.repositories.UsuarioRepository;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class EntregaEvidenciaService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "png", "jpg", "jpeg", "docx");

    private final EntregaEvidenciaRepository evidenciaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final Path uploadDir;

    public EntregaEvidenciaService(EntregaEvidenciaRepository evidenciaRepository,
                                   ProyectoRepository proyectoRepository,
                                   UsuarioRepository usuarioRepository,
                                   CotizacionRepository cotizacionRepository,
                                   @Value("${app.uploads.evidences-dir:uploads/evidences}") String uploadDir) {
        this.evidenciaRepository = evidenciaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public EvidenciaResponse upload(Long projectId, Long profesionalId, MultipartFile file, String descripcion) {
        validarArchivo(file);
        String descripcionLimpia = validarDescripcion(descripcion);

        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        if (proyecto.getEstado() != EstadoProyecto.EN_CONTRATO
                && proyecto.getEstado() != EstadoProyecto.EN_PROCESO
                && proyecto.getEstado() != EstadoProyecto.EN_REVISION) {
            throw new BusinessException("Solo se pueden subir evidencias para proyectos EN_CONTRATO, EN_PROCESO o EN_REVISION");
        }

        Usuario profesional = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + profesionalId));

        if (profesional.getRol() != RolUsuario.PROFESIONAL) {
            throw new ForbiddenException("Solo los profesionales pueden subir evidencias");
        }

        boolean tieneCotizacionAceptada = cotizacionRepository
                .existsByProyectoIdAndProfesionalIdAndEstado(projectId, profesionalId, EstadoCotizacion.ACEPTADA);

        if (!tieneCotizacionAceptada) {
            throw new ForbiddenException("Solo el profesional con cotización aceptada puede subir evidencias");
        }

        StoredFile stored = storeFile(file);

        EntregaEvidencia evidencia = new EntregaEvidencia();
        evidencia.setProyecto(proyecto);
        evidencia.setProfesional(profesional);
        evidencia.setNombreArchivo(stored.originalName());
        evidencia.setNombreAlmacenado(stored.storedName());
        evidencia.setTipoArchivo(stored.contentType());
        evidencia.setRutaArchivo(stored.path().toString());
        evidencia.setDescripcion(descripcionLimpia);
        evidencia.setEstado(EstadoEvidencia.ENVIADA);

        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listForProjectAsClient(Long projectId, Long clienteId) {
        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        if (!proyecto.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede ver evidencias de este proyecto");
        }

        return evidenciaRepository.findByProyectoIdOrderByFechaSubidaDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listMineAsProfessional(Long profesionalId) {
        return evidenciaRepository.findByProfesionalIdOrderByFechaSubidaDesc(profesionalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DownloadFile download(Long evidenceId, Long currentUserId, RolUsuario currentRol) {
        EntregaEvidencia evidencia = findAuthorized(evidenceId, currentUserId, currentRol);
        Path filePath = Paths.get(evidencia.getRutaArchivo()).toAbsolutePath().normalize();

        if (!filePath.startsWith(uploadDir)) {
            throw new ForbiddenException("Ruta de archivo no permitida");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("El archivo físico de la evidencia no existe");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("No se puede leer el archivo de evidencia");
            }
            return new DownloadFile(resource, evidencia.getNombreArchivo(), evidencia.getTipoArchivo());
        } catch (MalformedURLException e) {
            throw new BusinessException("No se pudo preparar la descarga del archivo");
        }
    }

    @Transactional
    public EvidenciaResponse approve(Long evidenceId, Long clienteId) {
        EntregaEvidencia evidencia = evidenciaRepository.findWithRelationsById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada con id: " + evidenceId));

        if (!evidencia.getProyecto().getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede aprobar esta evidencia");
        }

        evidencia.setEstado(EstadoEvidencia.APROBADA);
        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Transactional
    public EvidenciaResponse reject(Long evidenceId, Long clienteId) {
        EntregaEvidencia evidencia = evidenciaRepository.findWithRelationsById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada con id: " + evidenceId));

        if (!evidencia.getProyecto().getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede rechazar esta evidencia");
        }

        evidencia.setEstado(EstadoEvidencia.RECHAZADA);
        return toResponse(evidenciaRepository.save(evidencia));
    }

    private EntregaEvidencia findAuthorized(Long evidenceId, Long currentUserId, RolUsuario currentRol) {
        EntregaEvidencia evidencia = evidenciaRepository.findWithRelationsById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada con id: " + evidenceId));

        switch (currentRol) {
            case CLIENTE -> {
                if (!evidencia.getProyecto().getCliente().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para descargar esta evidencia");
                }
            }
            case PROFESIONAL -> {
                if (!evidencia.getProfesional().getId().equals(currentUserId)) {
                    throw new ForbiddenException("No tiene permisos para descargar esta evidencia");
                }
            }
            case ADMIN -> {
                // ADMIN puede descargar evidencias.
            }
        }
        return evidencia;
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo de evidencia es obligatorio");
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

    private String validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isBlank()) {
            throw new BusinessException("La descripción de la evidencia es obligatoria");
        }
        String limpia = descripcion.trim();
        if (limpia.length() < 10) {
            throw new BusinessException("La descripción debe tener al menos 10 caracteres");
        }
        if (limpia.length() > 1000) {
            throw new BusinessException("La descripción no puede superar 1000 caracteres");
        }
        return limpia;
    }

    private StoredFile storeFile(MultipartFile file) {
        try {
            Files.createDirectories(uploadDir);

            String original = sanitizeFilename(file.getOriginalFilename());
            String ext = extensionOf(original);
            String storedName = UUID.randomUUID() + "." + ext;
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
            throw new BusinessException("No se pudo guardar el archivo de evidencia");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "evidencia";
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

    private EvidenciaResponse toResponse(EntregaEvidencia evidencia) {
        EvidenciaResponse response = new EvidenciaResponse();
        response.setId(evidencia.getId());
        response.setProyectoId(evidencia.getProyecto().getId());
        response.setProyectoTitulo(evidencia.getProyecto().getTitulo());
        response.setProfesionalId(evidencia.getProfesional().getId());
        response.setProfesionalNombre(evidencia.getProfesional().getNombre());
        response.setNombreArchivo(evidencia.getNombreArchivo());
        response.setTipoArchivo(evidencia.getTipoArchivo());
        response.setDescripcion(evidencia.getDescripcion());
        response.setFechaSubida(evidencia.getFechaSubida());
        response.setEstado(evidencia.getEstado());
        response.setUrlDescarga("/api/v1/evidences/" + evidencia.getId() + "/download");
        return response;
    }

    private record StoredFile(String originalName, String storedName, String contentType, Path path) {}

    public record DownloadFile(Resource resource, String filename, String contentType) {}
}
