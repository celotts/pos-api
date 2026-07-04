package com.posapi.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para respuestas de la API.
 *
 * @param <T> el tipo de datos en la respuesta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    /**
     * Constructor para respuesta exitosa con datos.
     *
     * @param success el indicador de éxito
     * @param message el mensaje
     * @param data    los datos
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Crea una respuesta exitosa.
     *
     * @param <T>     el tipo de datos
     * @param message el mensaje
     * @param data    los datos
     * @return la respuesta
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Crea una respuesta exitosa sin datos.
     *
     * @param <T>     el tipo de datos
     * @param message el mensaje
     * @return la respuesta
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Crea una respuesta de error.
     *
     * @param <T>       el tipo de datos
     * @param message   el mensaje
     * @param errorCode el código de error
     * @return la respuesta
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}
