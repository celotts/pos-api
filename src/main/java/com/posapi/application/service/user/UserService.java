package com.posapi.application.service.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
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

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Puedes añadir métodos para actualizar usuario, cambiar contraseña, etc.
}
