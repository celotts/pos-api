package com.posapi.application.service.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class RoleService implements RoleManagementPort {

    private final RoleRepository roleRepository;
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateResourceException("Role with name '" + role.getName() + "' already exists.");
        }
        
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        role.setId(UUID.randomUUID());
        role.setCreatedBy(currentUser.getId());
        
        return roleRepository.save(role);
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
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        return roleRepository.findById(id).map(existingRole -> {
            existingRole.setName(role.getName());
            existingRole.setUpdatedBy(currentUser.getId());
            return roleRepository.save(existingRole);
        });
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleResponse> getRoleByIdWithUserNames(UUID id) {
        return roleRepository.findById(id).map(this::mapToRoleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRolesWithUserNames() {
        List<Role> roles = roleRepository.findAll();
        Set<UUID> userIds = roles.stream()
                .flatMap(role -> Stream.of(role.getCreatedBy(), role.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        return roles.stream()
                .map(role -> toResponse(role, userNames))
                .collect(Collectors.toList());
    }

    private RoleResponse mapToRoleResponse(Role role) {
        Set<UUID> userIds = Stream.of(role.getCreatedBy(), role.getUpdatedBy())
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

    private RoleResponse toResponse(Role role, Map<UUID, String> userNames) {
        String createdByName = userNames.get(role.getCreatedBy());
        String updatedByName = userNames.get(role.getUpdatedBy());
        return RoleResponse.fromDomain(role, createdByName, updatedByName);
    }
}
