package com.posapi.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando existe un conflicto en el estado del recurso (HTTP 409 Conflict).
 * Común en casos de registros duplicados o violaciones de restricciones de negocio.
 */
public class ConflictException extends ApplicationException {

    /**
     * Constructor para ConflictException.
     *
     * @param message el mensaje de error descriptivo
     */
    public ConflictException(final String message) {
        super(message, HttpStatus.CONFLICT);
    }

    /**
     * Constructor para ConflictException con causa raíz.
     *
     * @param message el mensaje de error descriptivo
     * @param cause   la excepción original que provocó este fallo
     */
    public ConflictException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
