package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.responses.EvidenciaResponse;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.EntregaEvidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Evidencias", description = "Evidencias y documentos de entrega")
public class EntregaEvidenciaController {

    private final EntregaEvidenciaService evidenciaService;

    public EntregaEvidenciaController(EntregaEvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @PostMapping(value = "/projects/{projectId}/evidences", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESIONAL')")
    @Operation(summary = "Subir evidencia de entrega para un proyecto contratado")
    public ResponseEntity<EvidenciaResponse> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("descripcion") String descripcion) {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        EvidenciaResponse response = evidenciaService.upload(projectId, profesionalId, file, descripcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/evidences")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Listar evidencias de un proyecto propio")
    public ResponseEntity<List<EvidenciaResponse>> listForProject(@PathVariable Long projectId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(evidenciaService.listForProjectAsClient(projectId, clienteId));
    }

    @GetMapping("/evidences/my")
    @PreAuthorize("hasRole('PROFESIONAL')")
    @Operation(summary = "Listar evidencias subidas por el profesional autenticado")
    public ResponseEntity<List<EvidenciaResponse>> listMine() {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(evidenciaService.listMineAsProfessional(profesionalId));
    }

    @GetMapping("/evidences/{evidenceId}/download")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Descargar una evidencia con validación de permisos")
    public ResponseEntity<Resource> download(@PathVariable Long evidenceId) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        RolUsuario rol = currentRol(principal);
        EntregaEvidenciaService.DownloadFile file = evidenciaService.download(evidenceId, principal.getId(), rol);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.resource());
    }

    @PutMapping("/evidences/{evidenceId}/approve")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Aprobar evidencia de un proyecto propio")
    public ResponseEntity<EvidenciaResponse> approve(@PathVariable Long evidenceId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(evidenciaService.approve(evidenceId, clienteId));
    }

    @PutMapping("/evidences/{evidenceId}/reject")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Rechazar evidencia de un proyecto propio")
    public ResponseEntity<EvidenciaResponse> reject(@PathVariable Long evidenceId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(evidenciaService.reject(evidenceId, clienteId));
    }

    private RolUsuario currentRol(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(RolUsuario::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene rol válido"));
    }
}
