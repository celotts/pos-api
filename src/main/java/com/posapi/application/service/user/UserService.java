package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.audit.AuditAction;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.aspect.Auditable;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Slf4j
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    @Auditable(action = AuditAction.INSERT, tableName = "users")
    public User createUser(User user) {
        log.debug("Attempting to create a new user with email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User creation failed: email {} already exists.", user.getEmail());
            throw new DuplicateResourceException(
                    "An account with this email already exists: " + user.getEmail());
        }

        // CORREGIDO: Obtener el objeto Role completo
        Role role = roleRepository.findById(user.getRole().getId()) // Acceder al ID del rol a través del objeto Role
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role with ID '" + user.getRole().getId() + "' not found.")); // Acceder al ID del rol a través del objeto Role

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(User::getId)
                .orElse(null);

        // CORREGIDO: Usar el método createNew de User con el objeto Role
        User userToSave = User.createNew(
                user.getEmail(), encodedPassword, user.getFullName(), role, user.getIsActive(), currentUserId);

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
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @Auditable(action = AuditAction.UPDATE, tableName = "users")
    public Optional<User> updateUser(UUID id, User userWithUpdates) {
        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(User::getId)
                .orElse(null);

        return userRepository.findById(id).map(existingUser -> {
            validateEmailOnUpdate(existingUser, userWithUpdates);
            Role finalRole = validateRoleOnUpdate(existingUser, userWithUpdates);
            String finalPassword = preparePasswordOnUpdate(existingUser, userWithUpdates);

            User userToUpdate = existingUser.updateWith(userWithUpdates, finalPassword, finalRole, currentUserId);

            return userRepository.save(userToUpdate);
        });
    }

    @Override
    @Transactional
    @Auditable(action = AuditAction.DELETE, tableName = "users")
    public boolean deleteUser(UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(User::getId)
                .orElse(null);

        return userRepository.findById(id).map(user -> {
            log.warn("Soft-deleting user with ID: {}", id);
            user.setDeletedAt(Instant.now());
            user.setDeletedByUserId(currentUserId);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    private void validateEmailOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getEmail() != null && !partialUpdate.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userRepository.findByEmail(partialUpdate.getEmail()).ifPresent(u -> {
                throw new DuplicateResourceException("The email " + u.getEmail() + " is already taken.");
            });
        }
    }

    private Role validateRoleOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getRole() != null && !partialUpdate.getRole().getId().equals(existingUser.getRole().getId())) {
            return roleRepository.findById(partialUpdate.getRole().getId()) // Acceder al ID del rol a través del objeto Role
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role with ID " + partialUpdate.getRole().getId() + " does not exist."));
        }
        return existingUser.getRole();
    }

    private String preparePasswordOnUpdate(User existingUser, User partialUpdate) {
        if (partialUpdate.getPassword() != null && !partialUpdate.getPassword().isBlank()) {
            return passwordEncoder.encode(partialUpdate.getPassword());
        }
        return existingUser.getPassword();
    }
}
