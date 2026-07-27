package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.shared.dto.PageResponse;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.security.SecurityContextHelper; // AÑADIDO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements RoleManagementPort {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper; // AÑADIDO

    // =============================================================================
    // 1. CREACIÓN DE ROLES
    // =============================================================================

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException("Role with name '" + request.name() + "' already exists.");
        }

        String upperCaseName = request.name().toUpperCase();
        if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
            throw new DuplicateResourceException("Cannot create a role with the reserved name '"
                    + request.name() + "'.");
        }

        UUID currentUserId = securityContextHelper.getCurrentUserId();
        UUID currentUserRoleId = securityContextHelper.getCurrentUserRoleId();

        Role newRole = Role.createNew(request.name(), currentUserId, currentUserRoleId);

        Role savedRole = roleRepository.save(newRole);
        Map<UUID, String> userNames = fetchUserNamesForAudit(Set.of(currentUserId));
        return toResponse(savedRole, userNames);
    }

    // =============================================================================
    // 2. LECTURA Y CONSULTAS
    // =============================================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleResponse> getRoleById(UUID id) {
        return roleRepository.findById(id)
                .map(role -> {
                    Set<UUID> userIds = Stream.of(role.getCreatedByUserId(),
                                    role.getUpdatedByUserId(),
                                    role.getDeletedByUserId())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    Map<UUID, String> userNames = fetchUserNamesForAudit(userIds);
                    return toResponse(role, userNames);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        Set<UUID> userIds = roles.stream()
                .flatMap(r -> Stream.of(r.getCreatedByUserId(), r.getUpdatedByUserId(), r.getDeletedByUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNamesForAudit(userIds);

        return roles.stream()
                .map(role -> toResponse(role, userNames))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> getAllRoles(Pageable pageable) {
        Page<Role> rolesPage = roleRepository.findAll(pageable);

        Set<UUID> userIds = rolesPage.getContent().stream()
                .flatMap(r -> Stream.of(r.getCreatedByUserId(), r.getUpdatedByUserId(), r.getDeletedByUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNamesForAudit(userIds);

        List<RoleResponse> content = rolesPage.getContent().stream()
                .map(role -> toResponse(role, userNames))
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                rolesPage.getNumber(),
                rolesPage.getSize(),
                rolesPage.getTotalElements(),
                rolesPage.getTotalPages(),
                rolesPage.isLast()
        );
    }

    // =============================================================================
    // 3. ACTUALIZACIÓN DE ROLES
    // =============================================================================

    @Override
    @Transactional
    public Optional<RoleResponse> updateRole(UUID id, RoleRequest request) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!existingRole.getName().equalsIgnoreCase(request.name())) {
            String upperCaseName = request.name().toUpperCase();
            if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
                throw new DuplicateResourceException(
                        "Cannot rename a role to the reserved name '" + request.name() + "'.");
            }

            if (roleRepository.findByName(request.name()).isPresent()) {
                throw new DuplicateResourceException("Role with name '" + request.name() + "' already exists.");
            }
        }

        existingRole.setName(request.name());
        existingRole.setUpdatedAt(Instant.now());
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        UUID currentUserRoleId = securityContextHelper.getCurrentUserRoleId();
        existingRole.setUpdatedByUserId(currentUserId);
        existingRole.setUpdatedByRoleId(currentUserRoleId);

        Role updatedRole = roleRepository.save(existingRole);
        Map<UUID, String> userNames = fetchUserNamesForAudit(Set.of(currentUserId));
        return Optional.of(toResponse(updatedRole, userNames));
    }

    // =============================================================================
    // 4. ELIMINACIÓN DE ROLES
    // =============================================================================

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        roleRepository.findById(id)
                .ifPresent(existingRole -> {
                    UUID currentUserId = securityContextHelper.getCurrentUserId();
                    UUID currentUserRoleId = securityContextHelper.getCurrentUserRoleId();

                    existingRole.markAsDeleted(currentUserId, currentUserRoleId);
                    roleRepository.save(existingRole);
                    log.info("Role with id {} marked as deleted by user {}", id, currentUserId);
                });
    }

    // =============================================================================
    // 5. OPERACIONES COMPLEMENTARIAS Y AUXILIARES
    // =============================================================================

    @Transactional(readOnly = true)
    public Map<UUID, String> fetchUserNamesForAudit(Set<UUID> userIds) {
        Set<UUID> cleanIds = userIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (cleanIds.isEmpty()) {
            return Map.of();
        }

        return cleanIds.stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    /**
     * Mapeador interno para construir el RoleResponse con nombres resueltos.
     */
    private RoleResponse toResponse(Role role, Map<UUID, String> userNames) {
        String createdByName = userNames.getOrDefault(role.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(role.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(role.getDeletedByUserId(), null);
        return RoleResponse.fromDomain(role, createdByName, updatedByName, deletedByName);
    }
}
