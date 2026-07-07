package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando el acceso al recurso es prohibido (HTTP 403 Forbidden).
 * Se utiliza cuando el usuario está autenticado pero no posee los roles requeridos.
 */
public class ForbiddenException extends ApplicationException {

    /**
     * Constructor para ForbiddenException.
     *
     * @param message el mensaje de error descriptivo
     */
    public ForbiddenException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Constructor para ForbiddenException con causa raíz.
     *
     * @param message el mensaje de error descriptivo
     * @param cause   la excepción original que provocó este fallo
     */
    public ForbiddenException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
