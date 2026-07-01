package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando la solicitud es inválida.
 */
public class BadRequestException extends ApplicationException {

    /**
     * Constructor para BadRequestException.
     *
     * @param message el mensaje de error
     */
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructor para BadRequestException con causa.
     *
     * @param message el mensaje de error
     * @param cause   la causa de la excepción
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
