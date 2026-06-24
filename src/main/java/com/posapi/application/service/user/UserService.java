package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        // 1. 🛡️ Validar unicidad del email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists: " + user.getEmail());
        }

        // 2. Procesar valores de forma segura reconstruyendo el dominio si es inmutable
        String rawPassword = user.getPassword();
        String encodedPassword = (rawPassword != null && !rawPassword.trim().isEmpty())
                ? passwordEncoder.encode(rawPassword)
                : rawPassword;

        String finalRole = (user.getRoleName() == null || user.getRoleName().trim().isEmpty())
                ? "USER"
                : user.getRoleName();

        // 3. Creamos la instancia final enriquecida para enviar al puerto de salida
        User userToSave = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(encodedPassword)
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .failedLoginAttempts(0) // Inicializar contador de intentos fallidos
                .roleName(finalRole)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return userRepository.save(userToSave);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {

            // 🛡️ Validar unicidad del email si cambia
            String finalEmail = existingUser.getEmail();
            if (!existingUser.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
                if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email is already taken by another user: " + updatedUser.getEmail());
                }
                finalEmail = updatedUser.getEmail();
            }

            // Procesar contraseña nueva si aplica
            String newPassword = updatedUser.getPassword();
            String finalPassword = (newPassword != null && !newPassword.trim().isEmpty())
                    ? passwordEncoder.encode(newPassword)
                    : existingUser.getPassword();

            // Reconstruimos el usuario actualizado conservando fechas de creación e ID estables
            User userToUpdate = User.builder()
                    .id(existingUser.getId())
                    .email(finalEmail)
                    .password(finalPassword)
                    .fullName(updatedUser.getFullName())
                    .isActive(updatedUser.getIsActive())
                    .roleName(updatedUser.getRoleName())
                    .failedLoginAttempts(existingUser.getFailedLoginAttempts()) // Mantiene el contador de intentos
                    .createdAt(existingUser.getCreatedAt()) // Mantiene fecha original
                    .updatedAt(Instant.now()) // Actualiza la fecha de modificación
                    .build();

            return userRepository.save(userToUpdate);
        });
    }

    @Override
    public boolean deleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}