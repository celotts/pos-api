package com.posapi.domain.exception;

// Excepción para errores de lógica interna que nunca deberían ocurrir.
public class InvariantException extends RuntimeException {
    public InvariantException(String message) {
        super(message);
    }
}