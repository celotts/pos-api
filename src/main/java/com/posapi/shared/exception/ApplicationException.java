package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base para todas las excepciones de aplicación.
 */
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * Constructor para ApplicationException.
     *
     * @param message    el mensaje de error
     * @param httpStatus el estado HTTP correspondiente
     */
    public ApplicationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Constructor para ApplicationException con causa.
     *
     * @param message    el mensaje de error
     * @param cause      la causa de la excepción
     * @param httpStatus el estado HTTP correspondiente
     */
    public ApplicationException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Obtiene el estado HTTP asociado a esta excepción.
     *
     * @return el HttpStatus
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
