package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando un recurso solicitado no existe (HTTP 404 Not Found).
 */
public class ResourceNotFoundException extends ApplicationException {

    /**
     * Constructor para ResourceNotFoundException.
     *
     * @param message el mensaje de error descriptivo
     */
    public ResourceNotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Constructor para ResourceNotFoundException con causa raíz.
     *
     * @param message el mensaje de error descriptivo
     * @param cause   la excepción original que provocó este fallo
     */
    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
