package com.posapi.application.service.auth;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final int MAX_FAILED_ATTEMPTS = 3;

    public String login(String email, String password) {
        // Autentica con Spring Security. Si las credenciales son malas, lanza una excepción.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        // Si la autenticación es exitosa, procedemos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado después de una autenticación exitosa."));

        // Reiniciar contador de intentos fallidos si fue exitoso
        if (user.getFailedLoginAttempts() > 0) {
            resetFailedAttempts(user);
        }

        return jwtService.generateToken(user);
    }

    private void resetFailedAttempts(User user) {
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .roleId(user.getRoleId())
                .failedLoginAttempts(0) // Reiniciamos
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        userRepository.save(updatedUser);
    }
}