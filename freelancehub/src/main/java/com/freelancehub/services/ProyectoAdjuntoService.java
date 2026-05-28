package com.freelancehub.services;

import com.freelancehub.controllers.dtos.responses.ProyectoAdjuntoResponse;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.entities.ProyectoAdjunto;
import com.freelancehub.enums.EstadoCotizacion;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ForbiddenException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.repositories.CotizacionRepository;
import com.freelancehub.repositories.ProyectoAdjuntoRepository;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProyectoAdjuntoService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "png", "jpg", "jpeg", "docx");

    private final ProyectoAdjuntoRepository adjuntoRepository;
    private final ProyectoRepository proyectoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final Path uploadDir;

    public ProyectoAdjuntoService(ProyectoAdjuntoRepository adjuntoRepository,
                                  ProyectoRepository proyectoRepository,
                                  CotizacionRepository cotizacionRepository,
                                  @Value("${app.uploads.project-attachments-dir:uploads/project-attachments}") String uploadDir) {
        this.adjuntoRepository = adjuntoRepository;
        this.proyectoRepository = proyectoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public ProyectoAdjuntoResponse upload(Long projectId, Long clienteId, MultipartFile file) {
        validarArchivo(file);

        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        if (!proyecto.getCliente().getId().equals(clienteId)) {
            throw new ForbiddenException("Solo el cliente dueño puede adjuntar archivos a este proyecto");
        }

        StoredFile stored = storeFile(file);

        ProyectoAdjunto adjunto = new ProyectoAdjunto();
        adjunto.setProyecto(proyecto);
        adjunto.setCliente(proyecto.getCliente());
        adjunto.setNombreArchivo(stored.originalName());
        adjunto.setNombreAlmacenado(stored.storedName());
        adjunto.setTipoArchivo(stored.contentType());
        adjunto.setRutaArchivo(stored.path().toString());

        return toResponse(adjuntoRepository.save(adjunto));
    }

    @Transactional(readOnly = true)
    public List<ProyectoAdjuntoResponse> listForProject(Long projectId, Long currentUserId, RolUsuario currentRol) {
        Proyecto proyecto = proyectoRepository.findWithRelationsById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));

        validarPermisoProyecto(proyecto, currentUserId, currentRol);

        return adjuntoRepository.findByProyectoIdOrderByFechaSubidaDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DownloadFile download(Long attachmentId, Long currentUserId, RolUsuario currentRol) {
        ProyectoAdjunto adjunto = adjuntoRepository.findWithRelationsById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Adjunto no encontrado con id: " + attachmentId));

        validarPermisoProyecto(adjunto.getProyecto(), currentUserId, currentRol);

        Path filePath = Paths.get(adjunto.getRutaArchivo()).toAbsolutePath().normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new ForbiddenException("Ruta de archivo no permitida");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("El archivo físico del adjunto no existe");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("No se puede leer el archivo adjunto");
            }
            return new DownloadFile(resource, adjunto.getNombreArchivo(), adjunto.getTipoArchivo());
        } catch (MalformedURLException e) {
            throw new BusinessException("No se pudo preparar la descarga del archivo adjunto");
        }
    }

    private void validarPermisoProyecto(Proyecto proyecto, Long currentUserId, RolUsuario currentRol) {
        switch (currentRol) {
            case CLIENTE -> {
                if (!proyecto.getCliente().getId().equals(currentUserId)) {
                    throw new ForbiddenException("Solo el cliente dueño puede ver los adjuntos de este proyecto");
                }
            }
            case PROFESIONAL -> {
                boolean proyectoAbierto = proyecto.getEstado() == EstadoProyecto.ABIERTO;
                boolean cotizacionAceptada = cotizacionRepository.existsByProyectoIdAndProfesionalIdAndEstado(
                        proyecto.getId(), currentUserId, EstadoCotizacion.ACEPTADA);
                if (!proyectoAbierto && !cotizacionAceptada) {
                    throw new ForbiddenException("No tiene permisos para ver los adjuntos de este proyecto");
                }
            }
            case ADMIN -> {
                // ADMIN puede ver y descargar adjuntos.
            }
        }
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo adjunto es obligatorio");
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
            throw new BusinessException("No se pudo guardar el archivo adjunto del proyecto");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "adjunto";
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

    private ProyectoAdjuntoResponse toResponse(ProyectoAdjunto adjunto) {
        ProyectoAdjuntoResponse response = new ProyectoAdjuntoResponse();
        response.setId(adjunto.getId());
        response.setProyectoId(adjunto.getProyecto().getId());
        response.setProyectoTitulo(adjunto.getProyecto().getTitulo());
        response.setClienteId(adjunto.getCliente().getId());
        response.setClienteNombre(adjunto.getCliente().getNombre());
        response.setNombreArchivo(adjunto.getNombreArchivo());
        response.setTipoArchivo(adjunto.getTipoArchivo());
        response.setFechaSubida(adjunto.getFechaSubida());
        response.setUrlDescarga("/api/v1/projects/attachments/" + adjunto.getId() + "/download");
        return response;
    }

    private record StoredFile(String originalName, String storedName, String contentType, Path path) {}

    public record DownloadFile(Resource resource, String filename, String contentType) {}
}
