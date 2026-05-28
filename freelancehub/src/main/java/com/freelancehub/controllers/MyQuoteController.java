package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.responses.QuoteResponse;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.services.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Cotizaciones del profesional autenticado.
 *   GET /api/v1/quotes/my  → PROFESIONAL ve sus cotizaciones
 *
 * Separado de QuoteController porque ese usa /projects/{id}/quotes.
 */
@RestController
@RequestMapping("/api/v1/quotes")
@Tag(name = "Mis cotizaciones (profesional)", description = "Listado de cotizaciones del profesional autenticado")
public class MyQuoteController {

    private final QuoteService quoteService;

    public MyQuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PROFESIONAL')")
    @Operation(summary = "Listar las cotizaciones del profesional autenticado")
    public ResponseEntity<List<QuoteResponse>> listMine() {
        Long profesionalId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(quoteService.listMyQuotes(profesionalId));
    }
}
