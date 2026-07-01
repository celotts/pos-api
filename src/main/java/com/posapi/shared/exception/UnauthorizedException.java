package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando el usuario no está autorizado.
 */
public class UnauthorizedException extends ApplicationException {

    /**
     * Constructor para UnauthorizedException.
     *
     * @param message el mensaje de error
     */
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructor para UnauthorizedException con causa.
     *
     * @param message el mensaje de error
     * @param cause   la causa de la excepción
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
