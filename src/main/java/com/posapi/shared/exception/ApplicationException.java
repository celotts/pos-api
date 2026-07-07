package com.posapi.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Excepción base inmutable para todas las excepciones de la aplicación.
 * Diseñada para integrarse de forma limpia con el GlobalExceptionHandler.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    /**
     * -- GETTER --
     *  Obtiene el código de estado HTTP asociado a esta excepción.
     *
     * @return el HttpStatusCode
     */
    private final HttpStatusCode httpStatus;

    /**
     * Constructor para ApplicationException.
     *
     * @param message    el mensaje de error descriptivo
     * @param httpStatus el código o estado HTTP correspondiente a la falla
     */
    public ApplicationException(final String message, final HttpStatusCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Constructor para ApplicationException con causa raíz.
     *
     * @param message    el mensaje de error descriptivo
     * @param cause      la excepción original que provocó este fallo
     * @param httpStatus el código o estado HTTP correspondiente a la falla
     */
    public ApplicationException(final String message, final Throwable cause, final HttpStatusCode httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

}
