package com.posapi.application.service.auth;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Autentica un usuario y devuelve un token JWT si las credenciales son válidas.
     * Maneja la lógica de reintentos y bloqueo de cuenta.
     *
     * @param email    El email del usuario.
     * @param password La contraseña del usuario.
     * @return Un token JWT.
     * @throws RuntimeException Si las credenciales son inválidas o la cuenta está bloqueada.
     */
    public String login(String email, String password) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(email)
                // Se lanza una excepción genérica para no revelar si el usuario existe o no
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas."));

        // 2. Verificar si la cuenta está bloqueada
        if (!user.getIsActive()) {
            throw new RuntimeException("La cuenta del usuario está bloqueada.");
        }

        // 3. Verificar la contraseña
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Contraseña correcta: reiniciar contador de intentos fallidos
            if (user.getFailedLoginAttempts() > 0) {
                resetFailedAttempts(user);
            }
            // Generar y devolver el token
            return jwtService.generateToken(user);
        } else {
            // Contraseña incorrecta: manejar intento fallido
            handleFailedLoginAttempt(user);
            throw new RuntimeException("Credenciales inválidas.");
        }
    }

    private void handleFailedLoginAttempt(User user) {
        int newAttemptCount = user.getFailedLoginAttempts() + 1;
        boolean shouldLockAccount = newAttemptCount >= MAX_FAILED_ATTEMPTS;

        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .roleId(user.getRoleId()) // Usa el ID, es lo correcto
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .failedLoginAttempts(newAttemptCount)
                .isActive(!shouldLockAccount)
                .build();

        userRepository.save(updatedUser);
    }

    private void resetFailedAttempts(User user) {
        // 1. Eliminamos .roleName(user.getRoleName())
        // 2. Usamos .roleId(user.getRoleId()) para mantener la relación
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .roleId(user.getRoleId()) // <--- Usamos el ID en lugar del nombre
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .failedLoginAttempts(0)
                .isActive(user.getIsActive())
                .build();

        userRepository.save(updatedUser);
    }
}