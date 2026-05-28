package com.freelancehub.controllers.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Wrapper genérico de respuesta para la API, alineado con el proyecto de
 * referencia del docente ({@code relation-mapping}).
 *
 * <p><b>Importante (Fase 1):</b> esta clase queda <em>disponible</em> pero
 * <em>no</em> se aplica todavía a los controllers existentes. Aplicarla ahora
 * cambiaría el shape del JSON que el frontend ya está consumiendo. La adopción
 * gradual del wrapper se hará en una fase posterior y de manera coordinada con
 * el frontend (probablemente empezando por {@code /api/v1/auth/**}).
 *
 * <p>El paquete {@code com.freelancehub.controllers.dtos.responses} se crea
 * <em>nuevo</em>; no se mueve ningún DTO existente todavía. Los DTOs actuales
 * siguen en sus paquetes {@code com.freelancehub.dto.*}.
 *
 * <h2>Estructura del JSON resultante</h2>
 * <pre>
 * {
 *   "success": true,
 *   "status": 200,
 *   "message": "Operación exitosa",
 *   "data": { ... },
 *   "timestamp": "2026-05-25T18:30:00Z"
 * }
 * </pre>
 *
 * <h2>Uso futuro previsto</h2>
 * <pre>
 * return ResponseEntity.ok(ApiResponse.ok(userResponse, "Login exitoso"));
 * return ResponseEntity.status(HttpStatus.CREATED)
 *     .body(ApiResponse.created(projectResponse, "Proyecto creado"));
 * </pre>
 *
 * @param <T> tipo del payload que viaja en el campo {@code data}
 */
@Getter
@Builder
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private Instant timestamp;

    /**
     * Respuesta exitosa estándar (HTTP 200 OK).
     */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Respuesta de recurso creado (HTTP 201 Created).
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Respuesta de error genérica.
     *
     * <p>Permite reutilizar el mismo wrapper para errores cuando se quiera un
     * formato uniforme, sin tener que ir al {@code ApiErrorResponse} clásico.
     * Útil principalmente en endpoints que ya estén envueltos con
     * {@code ApiResponse} y necesiten reportar un error con el mismo shape.
     *
     * @param message descripción del error
     * @param status  código HTTP a reportar dentro del cuerpo (la respuesta
     *                HTTP en sí debe fijarse con {@code ResponseEntity.status(...)})
     */
    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }
}
