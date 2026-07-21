package com.posapi.application.service.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.audit.AuditAction;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.PasswordEncoderPort;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import com.posapi.infrastructure.aspect.Auditable;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserManagementPort {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecurityContextHelper securityContextHelper;
    private final UserRestMapper userRestMapper;

    @Override
    @Transactional
    @Auditable(action = AuditAction.INSERT, tableName = "users")
    public UserResponse createUser(UserRequest userRequest) {
        log.debug("Attempting to create a new user with email: {}", userRequest.email());
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            log.warn("User creation failed: email {} already exists.", userRequest.email());
            throw new DuplicateResourceException(
                    "An account with this email already exists: " + userRequest.email());
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newUserRole = Role.createNew("USER", null, null); // createdByUserId y createdByRoleId pueden ser null para roles por defecto
                    return roleRepository.save(newUserRole);
                });

        User newUser = User.createNew(
                userRequest.email(),
                passwordEncoder.encode(userRequest.password()),
                userRequest.fullName(),
                userRole,
                true, // Por defecto activo al crear
                securityContextHelper.getCurrentUserId() // Usar el ID del usuario actual si está disponible
        );

        User savedUser = userRepository.save(newUser);
        log.info("Successfully created new user with ID: {}", savedUser.getId());
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponse> content = usersPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
    }

    @Override
    @Transactional
    @Auditable(action = AuditAction.UPDATE, tableName = "users")
    public Optional<UserResponse> updateUser(UUID id, UserRequest userRequest) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();

        return userRepository.findById(id).map(existingUser -> {
            validateEmailOnUpdate(existingUser, userRequest);
            Role finalRole = validateRoleOnUpdate(existingUser, userRequest);
            String finalPassword = preparePasswordOnUpdate(existingUser, userRequest);

            User userToUpdate = existingUser.updateWith(
                    User.builder()
                            .email(userRequest.email())
                            .fullName(userRequest.fullName())
                            .isActive(userRequest.isActive() != null ? userRequest.isActive() : existingUser.getIsActive())
                            .build(),
                    finalPassword,
                    finalRole,
                    currentUserId
            );

            User savedUser = userRepository.save(userToUpdate);
            return mapToUserResponse(savedUser);
        });
    }

    @Override
    @Transactional
    @Auditable(action = AuditAction.DELETE, tableName = "users")
    public void deleteUser(UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        UUID currentUserRoleId = securityContextHelper.getCurrentUserRoleId();

        userRepository.findById(id).ifPresent(user -> {
            log.warn("Soft-deleting user with ID: {}", id);
            user.markAsDeleted(currentUserId, currentUserRoleId);
            userRepository.save(user);
        });
    }

    private void validateEmailOnUpdate(User existingUser, UserRequest userRequest) {
        if (userRequest.email() != null && !userRequest.email().equalsIgnoreCase(existingUser.getEmail())) {
            userRepository.findByEmail(userRequest.email()).ifPresent(u -> {
                throw new DuplicateResourceException("The email " + u.getEmail() + " is already taken.");
            });
        }
    }

    private Role validateRoleOnUpdate(User existingUser, UserRequest userRequest) {
        if (userRequest.roleId() != null && !userRequest.roleId().equals(existingUser.getRole().getId())) {
            return roleRepository.findById(userRequest.roleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role with ID " + userRequest.roleId() + " does not exist."));
        }
        return existingUser.getRole();
    }

    private String preparePasswordOnUpdate(User existingUser, UserRequest userRequest) {
        if (userRequest.password() != null && !userRequest.password().isBlank()) {
            return passwordEncoder.encode(userRequest.password());
        }
        return existingUser.getPassword();
    }

    private UserResponse mapToUserResponse(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        return userRestMapper.toResponse(user, roleName, null, null); // COMPLETED: Added missing arguments and semicolon
    }
}
