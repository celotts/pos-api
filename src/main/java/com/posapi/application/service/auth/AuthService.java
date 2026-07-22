package com.posapi.application.service.auth;

import com.posapi.application.port.auth.AuthManagementPort;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
import com.posapi.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthManagementPort {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            // Si la autenticación es exitosa, generamos el token
            String jwt = jwtService.generateToken(authentication);
            return new LoginResponse(jwt);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", request.email(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password"); // Mensaje genérico por seguridad
        }
    }
}
