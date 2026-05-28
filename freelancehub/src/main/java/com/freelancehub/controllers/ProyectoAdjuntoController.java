package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.responses.ProyectoAdjuntoResponse;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.ProyectoAdjuntoService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Adjuntos de proyecto", description = "Archivos opcionales con requisitos o instrucciones del proyecto")
public class ProyectoAdjuntoController {

    private final ProyectoAdjuntoService adjuntoService;

    public ProyectoAdjuntoController(ProyectoAdjuntoService adjuntoService) {
        this.adjuntoService = adjuntoService;
    }

    @PostMapping(value = "/projects/{projectId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Subir archivo adjunto a un proyecto propio")
    public ResponseEntity<ProyectoAdjuntoResponse> upload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        ProyectoAdjuntoResponse response = adjuntoService.upload(projectId, clienteId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/attachments")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Listar adjuntos de un proyecto con validación de permisos")
    public ResponseEntity<List<ProyectoAdjuntoResponse>> listForProject(@PathVariable Long projectId) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        return ResponseEntity.ok(adjuntoService.listForProject(projectId, principal.getId(), currentRol(principal)));
    }

    @GetMapping("/projects/attachments/{attachmentId}/download")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Descargar adjunto de proyecto con validación de permisos")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        ProyectoAdjuntoService.DownloadFile file = adjuntoService.download(attachmentId, principal.getId(), currentRol(principal));

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.resource());
    }

    private RolUsuario currentRol(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(RolUsuario::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene rol válido"));
    }
}
