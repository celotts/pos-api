package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 */
public class ResourceNotFoundException extends ApplicationException {

    /**
     * Constructor para ResourceNotFoundException.
     *
     * @param message el mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Constructor para ResourceNotFoundException con causa.
     *
     * @param message el mensaje de error
     * @param cause   la causa de la excepción
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
