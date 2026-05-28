package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.request.HabilidadRequest;
import com.freelancehub.controllers.dtos.responses.HabilidadResponse;
import com.freelancehub.services.HabilidadService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/habilidades")
@Tag(name = "Habilidades",
        description = "Catálogo de habilidades. Lectura para usuarios autenticados, escritura solo ADMIN.")
public class HabilidadController {

    private final HabilidadService habilidadService;

    public HabilidadController(HabilidadService habilidadService) {
        this.habilidadService = habilidadService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar habilidades activas")
    public ResponseEntity<List<HabilidadResponse>> list() {
        return ResponseEntity.ok(habilidadService.listActive());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener habilidad por id")
    public ResponseEntity<HabilidadResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(habilidadService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear habilidad (solo ADMIN)")
    public ResponseEntity<HabilidadResponse> create(@Valid @RequestBody HabilidadRequest request) {
        HabilidadResponse response = habilidadService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar habilidad (solo ADMIN)")
    public ResponseEntity<HabilidadResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HabilidadRequest request) {
        return ResponseEntity.ok(habilidadService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar habilidad (solo ADMIN). Soft-delete: marca como inactiva.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        habilidadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
