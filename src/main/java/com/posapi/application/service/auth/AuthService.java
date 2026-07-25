package com.posapi.application.service.auth;

import com.posapi.application.port.auth.AuthManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
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
    private final UserRepository userRepository;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            // Si la autenticación es exitosa, generamos el token
            String jwt = jwtService.generateToken(authentication);

            // Obtener el usuario autenticado para construir UserResponse
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in repository"));

            // CORREGIDO: Construir UserResponse con los 10 argumentos correctos
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().getName(), // Usar el nombre del rol
                    user.getAddress(), // CORREGIDO
                    user.getPhone(),  // AÑADIDO
                    user.getPhone2(), // AÑADIDO
                    user.getIsActive(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );

            return new LoginResponse(jwt, userResponse);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", request.email(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password"); // Mensaje genérico por seguridad
        }
    }
}
