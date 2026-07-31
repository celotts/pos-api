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
import org.springframework.security.authentication.BadCredentialsException; // Importar BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Importar Collectors

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

            // Obtener UserDetails del objeto de autenticación
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 1. Extraer los roles/autoridades
            Map<String, Object> extraClaims = new HashMap<>();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority != null ? authority.getAuthority() : "")
                    .filter(role -> !role.isEmpty())
                    .collect(Collectors.toList()); // Usar collect(Collectors.toList())

            // 2. Guardar los roles en el token bajo la clave "roles"
            extraClaims.put("roles", roles);

            // 3. Generar el token JWT con los claims adicionales
            String jwt = jwtService.generateToken(extraClaims, userDetails);

            // Obtener el usuario autenticado para construir UserResponse
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in repository"));

            // Construir UserResponse con los 10 argumentos correctos
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().getName(), // Usar el nombre del rol
                    user.getAddress(),
                    user.getPhone(),
                    user.getPhone2(),
                    user.getIsActive(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );

            return new LoginResponse(jwt, userResponse);
        } catch (BadCredentialsException e) { // Capturar BadCredentialsException específicamente
            log.warn("Authentication failed for user {}: {}", request.email(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password"); // Mensaje genérico por seguridad
        } catch (AuthenticationException e) {
            log.warn("An unexpected authentication error occurred for user {}: {}", request.email(), e.getMessage());
            throw new IllegalArgumentException("Authentication failed");
        }
    }
}
