package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
@Service
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(@NotNull User user) {
        // Codificar la contraseña de forma segura si viene en el objeto
        String rawPassword = user.getPasswordHash();
        user.setPasswordHash(rawPassword != null ? passwordEncoder.encode(rawPassword) : "encodedPassword_fallback");

        // 🛡️ Validación robusta: Comprobar nulidad antes de procesar el String para evitar NPE
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }

        // Asegurar que el usuario esté activo por defecto
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // Asignar un ID si es nulo para mantener la consistencia

        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
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
    public Optional<User> updateUser(UUID id, @NotNull User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFullName(updatedUser.getFullName());
            // Solo actualizar passwordHash si se proporciona una nueva contraseña
            if (!updatedUser.getPasswordHash().isEmpty()) {
                existingUser.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(updatedUser.getPasswordHash())));
            }
            existingUser.setIsActive(updatedUser.getIsActive());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setUpdatedAt(Instant.now());
            return userRepository.save(existingUser);
        });
    }

    public boolean deleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
