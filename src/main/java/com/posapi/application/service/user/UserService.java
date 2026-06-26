package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import com.posapi.domain.repository.rol.RoleRepository;
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

    private static final String DEFAULT_ROLE_NAME = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists: " + user.getEmail());
        }

        String rawPassword = user.getPassword();
        String encodedPassword = (rawPassword != null && !rawPassword.trim().isEmpty())
                ? passwordEncoder.encode(rawPassword) : rawPassword;

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("El rol por defecto '" + DEFAULT_ROLE_NAME + "' no se encuentra."));

        User userToSave = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(encodedPassword)
                .fullName(user.getFullName())
                .isActive(true)
                .failedLoginAttempts(0)
                .roleId(defaultRole.getId()) // Se asigna el ID obtenido del repositorio
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return userRepository.save(userToSave);
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {

            // Si el DTO trae un nuevo roleId, validamos que exista antes de asignar
            UUID finalRoleId = existingUser.getRoleId();
            if (updatedUser.getRoleId() != null && !updatedUser.getRoleId().equals(existingUser.getRoleId())) {
                roleRepository.findById(updatedUser.getRoleId())
                        .orElseThrow(() -> new IllegalArgumentException("El rol con ID " + updatedUser.getRoleId() + " no existe."));
                finalRoleId = updatedUser.getRoleId();
            }

            User userToUpdate = User.builder()
                    .id(existingUser.getId())
                    .email(updatedUser.getEmail() != null ? updatedUser.getEmail() : existingUser.getEmail())
                    .password(existingUser.getPassword())
                    .fullName(updatedUser.getFullName())
                    .isActive(updatedUser.getIsActive() != null ? updatedUser.getIsActive() : existingUser.getIsActive())
                    .failedLoginAttempts(existingUser.getFailedLoginAttempts())
                    .roleId(finalRoleId) // Usamos el ID validado
                    .createdAt(existingUser.getCreatedAt())
                    .updatedAt(Instant.now())
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