package com.freelancehub.exception;

/**
 * Excepción para violaciones de reglas de negocio.
 *
 * <p>Alias compatible con el estilo del proyecto de referencia del docente
 * ({@code relation-mapping}). Hereda de {@link BusinessException} para que:
 * <ul>
 *   <li>El {@code GlobalExceptionHandler} existente la capture automáticamente
 *       (a través del handler de {@code BusinessException}).</li>
 *   <li>Conserve la misma anotación {@code @ResponseStatus(HttpStatus.CONFLICT)}
 *       heredada de la clase padre.</li>
 *   <li>Las nuevas clases puedan lanzar {@code BusinessRuleException} sin que
 *       el código existente deje de funcionar.</li>
 * </ul>
 *
 * <p>No reemplaza a {@link BusinessException}; ambas conviven y son intercambiables
 * para el manejador de excepciones global.
 */
public class BusinessRuleException extends BusinessException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
