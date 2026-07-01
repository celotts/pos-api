package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.audit.AuditAction;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.aspect.Auditable;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleManagementPort {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final RoleRepository roleRepository;
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @Auditable(action = AuditAction.INSERT, tableName = "roles")
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateResourceException("Role with name '" + role.getName() + "' already exists.");
        }
        
        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(com.posapi.domain.model.user.User::getId)
                .orElse(SYSTEM_USER_ID); // Usar un ID de sistema si no hay usuario

        Role roleToSave = Role.builder()
                .id(UUID.randomUUID())
                .name(role.getName())
                .createdBy(currentUserId) // Asignar el usuario
                .build();
        
        return roleRepository.save(roleToSave);
    }

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

    @Override
    @Transactional
    @Auditable(action = AuditAction.UPDATE, tableName = "roles")
    public Optional<Role> updateRole(UUID id, Role role) {
        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(com.posapi.domain.model.user.User::getId)
                .orElse(SYSTEM_USER_ID);

        return roleRepository.findById(id).map(existingRole -> {
            Role updatedRole = Role.builder()
                    .id(existingRole.getId())
                    .name(role.getName())
                    .createdAt(existingRole.getCreatedAt()) // Mantener el original
                    .createdBy(existingRole.getCreatedBy()) // Mantener el original
                    .updatedBy(currentUserId)
                    .build();
            return roleRepository.save(updatedRole);
        });
    }

    @Override
    @Transactional
    @Auditable(action = AuditAction.DELETE, tableName = "roles")
    public boolean deleteRole(UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .map(com.posapi.domain.model.user.User::getId)
                .orElse(SYSTEM_USER_ID);

        return roleRepository.findById(id).map(roleToDelete -> {
            roleToDelete.setDeletedAt(Instant.now());
            roleToDelete.setDeletedBy(currentUserId);
            roleToDelete.setUpdatedBy(currentUserId);
            roleRepository.save(roleToDelete);
            return true;
        }).orElse(false);
    }
}
