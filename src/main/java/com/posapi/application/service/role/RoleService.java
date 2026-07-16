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
import com.posapi.infrastructure.security.SecurityContextHelper;
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
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void assignRoleToUser(String userEmail, String roleName) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        // CORREGIDO: Establecer la entidad Role directamente en el objeto User
        user.setRole(role);
        // user.setRoleName(role.getName()); // ELIMINADO: Ya no es necesario si User tiene la entidad Role

        userRepository.save(user);
        log.info("Role {} assigned to user {}", roleName, userEmail);
    }

    @Override
    public RoleResponse createRole(RoleRequest request, UUID currentUserId) {
        return null;
    }

    @Transactional
    @Override
    public Role createRole(String roleName, UUID currentUserId) { // Firma actualizada
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new DuplicateResourceException("Role with name '" + roleName + "' already exists.");
        }

        String upperCaseName = roleName.toUpperCase();
        if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
            throw new DuplicateResourceException("Cannot create a role with the reserved name '" + roleName + "'.");
        }

        Role newRole = Role.builder()
                .name(roleName)
                .createdByUserId(currentUserId) // CORREGIDO: Usar createdByUserId
                .createdAt(Instant.now())
                .build();
        return roleRepository.save(newRole);
    }

    @Override
    public Role createRole(Role role) {
        return null;
    }

    // ELIMINADO: Método duplicado o incorrecto
    // @Override
    // public Role createRole(Role role) {
    //     return null;
    // }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // ELIMINADO: Método duplicado o incorrecto
    // @Override
    // public Optional<Role> updateRole(UUID id, Role role) {
    //     return Optional.empty();
    // }

    // ELIMINADO: Método duplicado o incorrecto
    // @Override
    // public void deleteRole(UUID id) {
    // }

    @Transactional
    @Override
    public Object updateRole(UUID roleId, String newRoleName, UUID currentUserId) { // Firma actualizada
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        if (!existingRole.getName().equalsIgnoreCase(newRoleName)) {
            String upperCaseName = newRoleName.toUpperCase();
            if ("ADMIN".equals(upperCaseName) || "USER".equals(upperCaseName)) {
                throw new DuplicateResourceException("Cannot rename a role to the reserved name '" + newRoleName + "'.");
            }

            if (roleRepository.findByName(newRoleName).isPresent()) {
                throw new DuplicateResourceException("Role with name '" + newRoleName + "' already exists.");
            }
        }

        existingRole.setName(newRoleName);
        existingRole.setUpdatedAt(Instant.now());
        existingRole.setUpdatedByUserId(currentUserId); // CORREGIDO: Usar setUpdatedByUserId
        return roleRepository.save(existingRole);
    }

    @Override // CORREGIDO: Añadido @Override para el método deleteRole(UUID roleId, UUID currentUserId)
    @Transactional
    public void deleteRole(UUID roleId, UUID currentUserId) { // Firma actualizada
        roleRepository.findById(roleId)
                .ifPresent(existingRole -> {
                    existingRole.setDeletedAt(Instant.now());
                    existingRole.setDeletedByUserId(currentUserId); // CORREGIDO: Usar setDeletedByUserId
                    roleRepository.save(existingRole);
                    log.info("Role with id {} marked as deleted by user {}", roleId, currentUserId);
                });
    }

    public Role getRoleDetails(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    @Override
    public List<RoleResponse> getAllRolesWithUserNames() { // Ajustado el tipo de retorno
        List<Role> roles = roleRepository.findAll();
        Set<UUID> userIds = roles.stream()
                .flatMap(r -> Stream.of(r.getCreatedByUserId(), r.getUpdatedByUserId(), r.getDeletedByUserId())) // CORREGIDO: Usar getCreatedByUserId, etc.
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);

        return roles.stream()
                .map(r -> toResponse(r, userNames))
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public Optional<RoleResponse> getRoleByIdWithUserNames(UUID id) { // Ajustado el tipo de retorno
        return roleRepository.findById(id).map(this::mapToRoleResponse);
    }

    @Override
    public Optional<Role> updateRole(UUID id, Role role) {
        return Optional.empty();
    }

    @Override
    public void deleteRole(UUID id) {

    }

    private RoleResponse mapToRoleResponse(Role role) { // Ajustado el tipo de retorno
        Set<UUID> userIds = Stream.of(role.getCreatedByUserId(), role.getUpdatedByUserId(), role.getDeletedByUserId()) // CORREGIDO: Usar getCreatedByUserId, etc.
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        return toResponse(role, userNames);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private RoleResponse toResponse(Role role, Map<UUID, String> userNames) { // Ajustado el tipo de retorno
        String createdByName = userNames.get(role.getCreatedByUserId()); // CORREGIDO: Usar getCreatedByUserId
        String updatedByName = userNames.get(role.getUpdatedByUserId()); // CORREGIDO: Usar getUpdatedByUserId
        String deletedByName = userNames.get(role.getDeletedByUserId()); // CORREGIDO: Usar getDeletedByUserId
        return RoleResponse.fromDomain(role, createdByName, updatedByName, deletedByName);
    }
}
