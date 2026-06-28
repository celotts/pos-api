package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        log.debug("Attempting to create a new user with email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User creation failed: email {} already exists.", user.getEmail());
            throw new DuplicateResourceException("An account with this email already exists: " + user.getEmail());
        }

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID '" + user.getRoleId() + "' not found."));

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        User userToSave = User.createNew(user.getEmail(), encodedPassword, user.getFullName(), role);

        // Devolvemos el resultado de la operación de guardado, que contiene las fechas
        User savedUser = userRepository.save(userToSave);
        log.info("Successfully created new user with ID: {}", savedUser.getId());
        return savedUser;
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
            validateEmailOnUpdate(existingUser, updatedUser);
            UUID finalRoleId = validateRoleOnUpdate(existingUser, updatedUser);
            String finalPassword = preparePasswordOnUpdate(existingUser, updatedUser);
            User userToUpdate = existingUser.updateWith(updatedUser, finalPassword, finalRoleId);
            return userRepository.save(userToUpdate);
        });
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            log.warn("Deleting user with ID: {}", id);
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateEmailOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getEmail() != null && !partialUpdate.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userRepository.findByEmail(partialUpdate.getEmail()).ifPresent(u -> {
                throw new DuplicateResourceException("The email " + u.getEmail() + " is already taken.");
            });
        }
    }

    private UUID validateRoleOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getRoleId() != null && !partialUpdate.getRoleId().equals(existingUser.getRoleId())) {
            roleRepository.findById(partialUpdate.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + partialUpdate.getRoleId() + " does not exist."));
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
