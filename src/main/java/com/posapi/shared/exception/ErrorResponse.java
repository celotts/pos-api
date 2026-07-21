package com.posapi.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus; // AÑADIDO: Importación de HttpStatus

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        HttpStatus status,
        int statusCode,
        String message,
        String path,
        Map<String, String> errors
) { }
