package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando el acceso es prohibido.
 */
public class ForbiddenException extends ApplicationException {

    /**
     * Constructor para ForbiddenException.
     *
     * @param message el mensaje de error
     */
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Constructor para ForbiddenException con causa.
     *
     * @param message el mensaje de error
     * @param cause   la causa de la excepción
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
