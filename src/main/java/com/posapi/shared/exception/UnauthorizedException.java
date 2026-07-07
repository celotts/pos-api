package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando las credenciales de autenticación faltan o son inválidas (HTTP 401 Unauthorized).
 */
public class UnauthorizedException extends ApplicationException {

    /**
     * Constructor para UnauthorizedException.
     *
     * @param message el mensaje de error descriptivo
     */
    public UnauthorizedException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructor para UnauthorizedException con causa raíz.
     *
     * @param message el mensaje de error descriptivo
     * @param cause   la excepción original que provocó este fallo
     */
    public UnauthorizedException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
