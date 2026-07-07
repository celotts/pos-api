package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando la solicitud del cliente es inválida (HTTP 400 Bad Request).
 */
public class BadRequestException extends ApplicationException {

    /**
     * Constructor para BadRequestException.
     *
     * @param message el mensaje de error descriptivo
     */
    public BadRequestException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructor para BadRequestException con causa raíz.
     *
     * @param message el mensaje de error descriptivo
     * @param cause   la excepción original que provocó este fallo
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
