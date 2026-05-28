package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.responses.PagoSimuladoResponse;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.PagoSimuladoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Pagos simulados", description = "Pago simulado tipo escrow para sustentación académica")
public class PagoSimuladoController {

    private final PagoSimuladoService pagoService;

    public PagoSimuladoController(PagoSimuladoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/projects/{projectId}/payments/simulate")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crear pago simulado retenido para un proyecto contratado")
    public ResponseEntity<PagoSimuladoResponse> create(@PathVariable Long projectId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        PagoSimuladoResponse response = pagoService.createForProject(projectId, clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/payments")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Consultar pago simulado de un proyecto")
    public ResponseEntity<PagoSimuladoResponse> getForProject(@PathVariable Long projectId) {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        return ResponseEntity.ok(pagoService.getForProject(projectId, principal.getId(), currentRol(principal)));
    }

    @PutMapping("/payments/{paymentId}/release")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Liberar pago simulado retenido después de aprobar evidencia")
    public ResponseEntity<PagoSimuladoResponse> release(@PathVariable Long paymentId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(pagoService.release(paymentId, clienteId));
    }

    @GetMapping("/payments/my")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Listar mis pagos simulados")
    public ResponseEntity<List<PagoSimuladoResponse>> listMine() {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        return ResponseEntity.ok(pagoService.listMine(principal.getId(), currentRol(principal)));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pagos simulados")
    public ResponseEntity<List<PagoSimuladoResponse>> listAll() {
        return ResponseEntity.ok(pagoService.listAll());
    }

    private RolUsuario currentRol(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .map(RolUsuario::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado no tiene rol válido"));
    }
}
