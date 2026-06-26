package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleManagementPort {


    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(UUID id, Role role) {
        Role existingRole = getRoleById(id);
        Role updatedRole = Role.builder()
                .id(existingRole.getId())
                .name(role.getName())
                .build();
        return roleRepository.save(updatedRole);
    }

    @Override
    public void deleteRole(UUID id) {
        if (roleRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Role not found with ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}