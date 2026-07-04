package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando existe un conflicto (ej: duplicado).
 */
public class ConflictException extends ApplicationException {

    /**
     * Constructor para ConflictException.
     *
     * @param message el mensaje de error
     */
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    /**
     * Constructor para ConflictException con causa.
     *
     * @param message el mensaje de error
     * @param cause   la causa de la excepción
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
