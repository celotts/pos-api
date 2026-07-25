package com.posapi.infrastructure.adapter.input.rest.error;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("ResourceNotFoundException: {}", ex.getMessage()); // Formateado para legibilidad
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null, // details (puede ser null para errores simples)
                Instant.now(),
                request.getDescription(false), // path (la URI de la solicitud)
                null // fieldErrors (no hay errores de campo para este tipo de excepción)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        log.warn("DuplicateResourceException: {}", ex.getMessage()); // Formateado para legibilidad
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null, // details (puede ser null para errores simples)
                Instant.now(),
                request.getDescription(false), // path
                null // fieldErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            // Cambiar el tipo de retorno
            MethodArgumentNotValidException ex, WebRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.ValidationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        Map<String, String> errorsMap = new HashMap<>(); // Para el log
        validationErrors.forEach(error -> errorsMap.put(error.getField(),
                error.getMessage())); // Formateado para legibilidad

        log.warn("Validation errors: {}", errorsMap); // Formateado para legibilidad

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed for argument(s).", // message
                "One or more fields in the request body are invalid.", // details
                Instant.now(),
                request.getDescription(false), // path
                validationErrors // fieldErrors (aquí sí se pasan los errores de campo mapeados)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex); // Formateado para legibilidad
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                ex.getMessage(), // details (mensaje interno de la excepción, para depuración)
                Instant.now(),
                request.getDescription(false), // path
                null // fieldErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
