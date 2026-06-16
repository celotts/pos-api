package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
        // Codificar la contraseña de forma segura si viene en el objeto
        String rawPassword = user.getPasswordHash();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        // 🛡️ Validación robusta: Comprobar nulidad antes de procesar el String para
        // evitar NPE
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }

        // Asegurar que el usuario esté activo por defecto
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // Note: createdAt and updatedAt are now handled automatically by
        // JpaAuditing via the Auditable mapped superclass.
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
    public Optional<User> updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFullName(updatedUser.getFullName());
            // Solo actualizar passwordHash si se proporciona una nueva contraseña
            String newPassword = updatedUser.getPasswordHash();
            if (!newPassword.trim().isEmpty()) {
                existingUser
                        .setPasswordHash(passwordEncoder.encode(newPassword));
            }
            existingUser.setIsActive(updatedUser.getIsActive());
            existingUser.setRole(updatedUser.getRole());
            return userRepository.save(existingUser);
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
