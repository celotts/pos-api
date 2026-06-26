package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleManagementPort {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateResourceException("Role with name '" + role.getName() + "' already exists.");
        }
        Role roleToSave = Role.builder().id(UUID.randomUUID()).name(role.getName()).build();
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
    public Optional<Role> updateRole(UUID id, Role role) {
        return roleRepository.findById(id).map(existingRole -> {
            Role updatedRole = Role.builder().id(existingRole.getId()).name(role.getName()).build();
            return roleRepository.save(updatedRole);
        });
    }

    @Override
    @Transactional
    public boolean deleteRole(UUID id) {
        if (roleRepository.findById(id).isPresent()) {
            roleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}