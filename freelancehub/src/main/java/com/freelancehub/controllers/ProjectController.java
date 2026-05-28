package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.request.CreateProjectRequest;
import com.freelancehub.controllers.dtos.responses.ProjectResponse;
import com.freelancehub.controllers.dtos.request.UpdateProjectRequest;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Proyectos", description = "Gestión de proyectos")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear un nuevo proyecto (solo CLIENTE)")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        ProjectResponse response = projectService.create(clienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los proyectos (solo ADMIN)")
    public ResponseEntity<List<ProjectResponse>> listAll() {
        return ResponseEntity.ok(projectService.listAll());
    }

    @GetMapping("/open")
    @PreAuthorize("hasRole('PROFESIONAL')")
    @Operation(summary = "Listar proyectos abiertos con filtros opcionales (solo PROFESIONAL)")
    public ResponseEntity<List<ProjectResponse>> listOpen(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget) {
        return ResponseEntity.ok(projectService.listOpen(categoryId, minBudget, maxBudget));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Listar los proyectos del cliente autenticado (solo CLIENTE)")
    public ResponseEntity<List<ProjectResponse>> listMy() {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(projectService.listMy(clienteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Obtener un proyecto por id con restricciones por rol")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        RolUsuario rol = principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(RolUsuario::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene rol válido"));

        return ResponseEntity.ok(projectService.getByIdForCurrentUser(id, principal.getId(), rol));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Actualizar proyecto propio (solo CLIENTE dueño)")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(projectService.update(id, clienteId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Eliminar proyecto propio (solo CLIENTE dueño)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        projectService.delete(id, clienteId);
        return ResponseEntity.noContent().build();
    }
}
