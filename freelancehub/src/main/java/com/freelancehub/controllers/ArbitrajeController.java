package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.request.ResolverArbitrajeRequest;
import com.freelancehub.controllers.dtos.responses.ArbitrajeResponse;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.ArbitrajeService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ArbitrajeController {

    private final ArbitrajeService arbitrajeService;

    public ArbitrajeController(ArbitrajeService arbitrajeService) {
        this.arbitrajeService = arbitrajeService;
    }

    @PostMapping(value = "/evidences/{evidenceId}/arbitrations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ArbitrajeResponse> create(
            @PathVariable Long evidenceId,
            @RequestParam("motivo") String motivo,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(arbitrajeService.create(evidenceId, clienteId, motivo, file));
    }

    @GetMapping("/arbitrations/my")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ArbitrajeResponse>> listMineAsClient() {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(arbitrajeService.listMineAsClient(clienteId));
    }

    @GetMapping("/arbitrations/professional/my")
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<List<ArbitrajeResponse>> listMineAsProfessional() {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(arbitrajeService.listMineAsProfessional(profesionalId));
    }

    @PostMapping(value = "/arbitrations/{arbitrationId}/response", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<ArbitrajeResponse> respond(
            @PathVariable Long arbitrationId,
            @RequestParam("respuesta") String respuesta,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(arbitrajeService.respond(arbitrationId, profesionalId, respuesta, file));
    }

    @GetMapping("/admin/arbitrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ArbitrajeResponse>> listAllForAdmin() {
        return ResponseEntity.ok(arbitrajeService.listAll());
    }

    @GetMapping("/admin/arbitrations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArbitrajeResponse> getForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(arbitrajeService.getForAdmin(id));
    }

    @PutMapping("/admin/arbitrations/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArbitrajeResponse> resolve(
            @PathVariable Long id,
            @Valid @RequestBody ResolverArbitrajeRequest request) {
        return ResponseEntity.ok(arbitrajeService.resolve(id, request));
    }

    @GetMapping("/arbitrations/{id}/client-file/download")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    public ResponseEntity<Resource> downloadClientFile(@PathVariable Long id) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        ArbitrajeService.DownloadFile file = arbitrajeService.downloadClientFile(id, principal.getId(), currentRol(principal));
        return downloadResponse(file);
    }

    @GetMapping("/arbitrations/{id}/professional-file/download")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    public ResponseEntity<Resource> downloadProfessionalFile(@PathVariable Long id) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        ArbitrajeService.DownloadFile file = arbitrajeService.downloadProfessionalFile(id, principal.getId(), currentRol(principal));
        return downloadResponse(file);
    }

    private ResponseEntity<Resource> downloadResponse(ArbitrajeService.DownloadFile file) {
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
