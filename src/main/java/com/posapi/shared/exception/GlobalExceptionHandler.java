package com.posapi.shared.exception;

import com.posapi.domain.exception.DuplicateResourceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejador para errores de validación de DTOs (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejador para duplicados de negocio (ej. RFC, nombre, etc.)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // 409 Conflict es más semántico
    }

    // Manejador para violaciones de integridad de la base de datos (ej. unique constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Extraer un mensaje más amigable si es posible
        String message = "A data integrity error occurred. A common cause is trying to duplicate a unique value.";
        if (ex.getMostSpecificCause() != null) {
            message = ex.getMostSpecificCause().getMessage();
        }
        ErrorResponse errorResponse = new ErrorResponse(message, null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    // Manejador para cualquier otra excepción no controlada (el que causa el 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // Log the exception for debugging purposes
        ex.printStackTrace(); 
        ErrorResponse errorResponse = new ErrorResponse("An unexpected internal server error occurred. Please contact support.", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Record para una respuesta de error estructurada
    public record ErrorResponse(String message, List<String> errors) {}
}
