package com.posapi.domain.exception;

// Exception for internal logic errors that should never occur.
public class InvariantException extends RuntimeException {
    public InvariantException(String message) {
        super(message);
    }
}