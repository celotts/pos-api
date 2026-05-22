package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import jakarta.validation.constraints.NotNull; // Importar NotNull
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @NotNull // Indica que este método siempre devuelve un User no nulo
    public User createUser(@NotNull User user) { // También marcamos el parámetro como NotNull
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(Instant.now());
        }
        // Codificar la contraseña antes de guardar
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // Asegurar que el rol por defecto sea USER si no se especifica
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        // Asegurar que el usuario esté activo por defecto
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Puedes añadir métodos para actualizar usuario, cambiar contraseña, etc.
}
