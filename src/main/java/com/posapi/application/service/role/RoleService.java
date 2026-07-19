package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.shared.exception.ResourceNotFoundException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // =============================================================================
    // 1. CREACIÓN DE ROLES
    // =============================================================================

    @Override
    @Transactional
    public Role createRole(String roleName, UUID currentUserId) {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new DuplicateResourceException("Role with name '" + roleName + "' already exists.");
        }

        String upperCaseName = roleName.toUpperCase();
        if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
            throw new DuplicateResourceException("Cannot create a role with the reserved name '" + roleName + "'.");
        }

        Role newRole = Role.builder()
                .id(UUID.randomUUID())
                .name(roleName)
                .createdByUserId(currentUserId)
                .createdAt(Instant.now())
                .build();

        return roleRepository.save(newRole);
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        // Lógica para guardar un objeto de dominio completo
        if (role.getId() == null) {
            role.setId(UUID.randomUUID());
        }
        if (role.getCreatedAt() == null) {
            role.setCreatedAt(Instant.now());
        }
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request, UUID currentUserId) {
        // CORREGIDO: Usamos request.name() que es el método nativo generado por el record
        Role role = createRole(request.name(), currentUserId);
        Map<UUID, String> userNames = fetchUserNamesForAudit(Set.of(currentUserId));
        return toResponse(role, userNames);
    }

    // =============================================================================
    // 2. LECTURA Y CONSULTAS (CON Y SIN NOMBRES DE AUDITORÍA)
    // =============================================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleDetails(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRolesWithUserNames() {
        List<Role> roles = roleRepository.findAll();

        // Colectamos todos los IDs involucrados en auditoría para hacer una sola consulta por lotes
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
    public Optional<RoleResponse> getRoleByIdWithUserNames(UUID id) {
        return roleRepository.findById(id)
                .map(role -> {
                    Set<UUID> userIds = Stream.of(role.getCreatedByUserId(), role.getUpdatedByUserId(), role.getDeletedByUserId())
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    Map<UUID, String> userNames = fetchUserNamesForAudit(userIds);
                    return toResponse(role, userNames);
                });
    }

    // =============================================================================
    // 3. ACTUALIZACIÓN DE ROLES (SOBRECARGAS)
    // =============================================================================

    @Override
    @Transactional
    public Optional<Role> updateRole(UUID id, Role role) {
        // Sobrecarga de compatibilidad que delega sin ID de usuario específico
        return updateRole(id, role, null);
    }

    @Override
    @Transactional
    public Optional<Role> updateRole(UUID id, Role role, UUID currentUserId) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!existingRole.getName().equalsIgnoreCase(role.getName())) {
            String upperCaseName = role.getName().toUpperCase();
            if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
                throw new DuplicateResourceException("Cannot rename a role to the reserved name '" + role.getName() + "'.");
            }

            if (roleRepository.findByName(role.getName()).isPresent()) {
                throw new DuplicateResourceException("Role with name '" + role.getName() + "' already exists.");
            }
        }

        existingRole.setName(role.getName());
        existingRole.setUpdatedAt(Instant.now());
        if (currentUserId != null) {
            existingRole.setUpdatedByUserId(currentUserId);
        }

        return Optional.of(roleRepository.save(existingRole));
    }

    @Override
    @Transactional
    public Object updateRole(UUID roleId, String newRoleName, UUID currentUserId) {
        Role template = Role.builder().name(newRoleName).build();
        return updateRole(roleId, template, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    // =============================================================================
    // 4. ELIMINACIÓN DE ROLES (SOBRECARGAS)
    // =============================================================================

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        // Sobrecarga de compatibilidad que delega sin ID de usuario específico
        deleteRole(id, null);
    }

    @Override
    @Transactional
    public void deleteRole(UUID id, UUID currentUserId) {
        roleRepository.findById(id)
                .ifPresent(existingRole -> {
                    existingRole.setDeletedAt(Instant.now());
                    if (currentUserId != null) {
                        existingRole.setDeletedByUserId(currentUserId);
                    }
                    roleRepository.save(existingRole);
                    log.info("Role with id {} marked as deleted by user {}", id, currentUserId);
                });
    }

    // =============================================================================
    // 5. OPERACIONES COMPLEMENTARIAS Y AUXILIARES
    // =============================================================================

    @Override
    @Transactional
    public void assignRoleToUser(String userEmail, String roleName) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        user.setRole(role);
        userRepository.save(user);
        log.info("Role {} assigned to user {}", roleName, userEmail);
    }

    @Override
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
        String createdByName = userNames.get(role.getCreatedByUserId());
        String updatedByName = userNames.get(role.getUpdatedByUserId());
        String deletedByName = userNames.get(role.getDeletedByUserId());
        return RoleResponse.fromDomain(role, createdByName, updatedByName, deletedByName);
    }
}
