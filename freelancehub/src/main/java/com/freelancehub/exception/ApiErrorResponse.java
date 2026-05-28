package com.freelancehub.exception;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta estándar de error siguiendo el estilo del proyecto de referencia
 * del docente ({@code relation-mapping}).
 *
 * <p><b>Importante:</b> esta clase <em>convive</em> con {@link ErrorResponse}; no la
 * reemplaza. {@link ErrorResponse} sigue siendo la respuesta que producen el
 * {@code GlobalExceptionHandler}, el {@code JwtAuthenticationEntryPoint}, el
 * {@code JwtAuthenticationFilter} y el {@code SecurityConfig} actuales. Esta
 * {@code ApiErrorResponse} queda <em>disponible</em> para fases posteriores en las
 * que el handler global se alinee con el formato del docente.
 *
 * <p>Diferencias respecto a {@link ErrorResponse}:
 * <ul>
 *   <li>Usa {@link Instant} en lugar de {@code LocalDateTime} (formato ISO-8601 UTC).</li>
 *   <li>No expone el campo {@code message} con alias JSON {@code "mensaje"}; aquí
 *       el JSON es {@code "message"} a secas, igual que el docente.</li>
 * </ul>
 *
 * <p>Mantengo getters/setters manuales (sin Lombok) intencionalmente: aunque
 * Lombok ya está añadido al POM, esta clase queda compilable incluso si el
 * procesador de anotaciones de Lombok tuviera algún problema, garantizando la
 * compatibilidad de la Fase 1.
 */
public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;

    public ApiErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ApiErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
