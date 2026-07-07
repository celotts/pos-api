package com.posapi.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Contrato unificado e inmutable para respuestas exitosas de la API.
 * Usando Java 21 Records para garantizar inmutabilidad nativa sin Lombok.
 *
 * @param <T> el tipo de datos envuelto en la respuesta
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    /**
     * Crea una respuesta estática exitosa con datos.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Crea una respuesta estática exitosa sin contenido en el cuerpo de datos.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }
}
