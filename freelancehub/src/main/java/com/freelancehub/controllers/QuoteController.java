package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.request.CreateQuoteRequest;
import com.freelancehub.controllers.dtos.responses.QuoteResponse;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.services.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Cotizaciones agrupadas por proyecto:
 *   POST /api/v1/projects/{id}/quotes  → PROFESIONAL crea cotización
 *   GET  /api/v1/projects/{id}/quotes  → CLIENTE dueño ve cotizaciones del proyecto
 */
@RestController
@RequestMapping("/api/v1/projects/{id}/quotes")
@Tag(name = "Cotizaciones (por proyecto)", description = "Crear y listar cotizaciones por proyecto")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESIONAL')")
    @Operation(summary = "Crear cotización para un proyecto ABIERTO (solo PROFESIONAL)")
    public ResponseEntity<QuoteResponse> create(
            @PathVariable("id") Long projectId,
            @Valid @RequestBody CreateQuoteRequest request) {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        QuoteResponse response = quoteService.createQuote(projectId, profesionalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Listar cotizaciones del proyecto (solo el cliente dueño)")
    public ResponseEntity<List<QuoteResponse>> listByProject(@PathVariable("id") Long projectId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(quoteService.listForProject(projectId, clienteId));
    }

    @PutMapping("/{quoteId}/accept")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Aceptar una cotización del proyecto (solo el cliente dueño)")
    public ResponseEntity<QuoteResponse> accept(
            @PathVariable("id") Long projectId,
            @PathVariable("quoteId") Long quoteId) {
        Long clienteId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(quoteService.acceptQuote(projectId, quoteId, clienteId));
    }
}
