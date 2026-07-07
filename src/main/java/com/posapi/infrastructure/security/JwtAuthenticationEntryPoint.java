package com.posapi.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.Serializable;

@Component("customJwtAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        final String expiredMsg = (String) request.getAttribute("expired");
        final String message = (expiredMsg != null) ? expiredMsg : "No autorizado: Token faltante o inválido";

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                String.format("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\" }", message)
        );
    }
}
