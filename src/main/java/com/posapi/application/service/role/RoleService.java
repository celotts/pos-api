package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.application.port.secondary.RoleOutputPort;
import com.posapi.domain.model.role.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService implements RoleManagementPort {

    private final RoleOutputPort roleOutputPort;

    public RoleService(RoleOutputPort roleOutputPort) {
        this.roleOutputPort = roleOutputPort;
    }

    @Override
    public Role createRole(Role role) {
        if (roleOutputPort.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }
        return roleOutputPort.save(role);
    }

    @Override
    public Role getRoleById(UUID id) {
        return roleOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleOutputPort.findAll();
    }

    @Override
    public Role updateRole(UUID id, Role role) {
        Role existingRole = getRoleById(id);
        Role updatedRole = Role.builder()
                .id(existingRole.getId())
                .name(role.getName())
                .build();
        return roleOutputPort.save(updatedRole);
    }

    @Override
    public void deleteRole(UUID id) {
        if (roleOutputPort.findById(id).isEmpty()) {
            throw new RuntimeException("Role not found");
        }
        roleOutputPort.deleteById(id);
    }
}
