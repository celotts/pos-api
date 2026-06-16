package com.posapi.application.payload;

/**
 * DTO inmutable para la respuesta de autenticación.
 * Incluye el token y el tipo para seguir el estándar de la industria.
 */
public record AuthenticationResponse(
    String token,
    String type,
    long expiresIn // Tiempo de vida en milisegundos
) {
    public AuthenticationResponse(String token) {
        this(token, "Bearer", 3600000); // Ejemplo: 1 hora por defecto
    }
}