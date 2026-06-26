package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists: " + user.getEmail()); // NOSONAR
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("El rol por defecto '" + DEFAULT_ROLE_NAME + "' no se encuentra."));

        // Delegate creation logic to the domain model's static factory method
        User userToSave = User.createNew(user.getEmail(), encodedPassword, user.getFullName(), defaultRole);

        return userRepository.save(userToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<User> updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {

            // 1. 🛡️ Perform validations
            validateEmailOnUpdate(existingUser, updatedUser);
            UUID finalRoleId = validateRoleOnUpdate(existingUser, updatedUser);

            // 2. 🛡️ Prepare derived data
            String finalPassword = preparePasswordOnUpdate(existingUser, updatedUser);

            // 3. 🛡️ Delegate update logic to the domain object
            User userToUpdate = existingUser.updateWith(updatedUser, finalPassword, finalRoleId);

            return userRepository.save(userToUpdate);
        });
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }

    // --- Private Helper Methods for Update Logic ---

    private void validateEmailOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getEmail() != null && !partialUpdate.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userRepository.findByEmail(partialUpdate.getEmail()).ifPresent(u -> {
                throw new IllegalArgumentException("The email " + u.getEmail() + " is already taken.");
            });
        }
    }

    private UUID validateRoleOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getRoleId() != null && !partialUpdate.getRoleId().equals(existingUser.getRoleId())) {
            roleRepository.findById(partialUpdate.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("El rol con ID " + partialUpdate.getRoleId() + " no existe."));
            return partialUpdate.getRoleId();
        }
        return existingUser.getRoleId();
    }

    private String preparePasswordOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getPassword() != null && !partialUpdate.getPassword().isBlank()) {
            return passwordEncoder.encode(partialUpdate.getPassword());
        }
        return existingUser.getPassword();
    }
}