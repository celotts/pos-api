package com.posapi.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Estructura estándar e inmutable para reportar errores hacia el frontend.
 * Agrupado por cohesión dentro del paquete de excepciones.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        String detail,
        Instant timestamp,
        String path,
        List<FieldError> errors
) {
    /**
     * Constructor compacto para rellenar automáticamente el timestamp si se requiere.
     */
    public ErrorResponse(int status, String message, String detail, String path, List<FieldError> errors) {
        this(status, message, detail, Instant.now(), path, errors);
    }

    /**
     * Método de factoría rápido para errores simples (ej. 400 Bad Request o 404 Not Found)
     */
    public static ErrorResponse of(int status, String message, String detail, String path) {
        return new ErrorResponse(status, message, detail, Instant.now(), path, null);
    }

    /**
     * Estructura inmutable para reportar fallos de validación de campos.
     */
    public record FieldError(
            String field,
            String message,
            Object rejectedValue
    ) { } // 💡 Corregido: Espacio en blanco añadido para cumplir la regla WhitespaceAround
}
