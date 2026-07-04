package com.posapi.infrastructure.adapter.input.rest.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.infrastructure.adapter.input.rest.role.mapper.RoleRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleManagementPort roleManagementPort;
    private final UserRepository userRepository;
    private final RoleRestMapper roleRestMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        User currentUser = getCurrentAuthenticatedUser();

        Role roleToCreate = roleRestMapper.toDomain(request);

        if (roleToCreate.getId() == null) {
            roleToCreate.setId(UUID.randomUUID());
        }

        roleToCreate.setCreatedBy(currentUser.getId());

        Role createdRole = roleManagementPort.createRole(roleToCreate);

        RoleResponse response = roleRestMapper.toResponse(
                createdRole,
                currentUser.getFullName(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        User currentUser = getCurrentAuthenticatedUser();

        Role roleToUpdate = Role.builder()
                .name(request.name())
                .updatedBy(currentUser.getId())
                .build();

        return roleManagementPort.updateRole(id, roleToUpdate)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        User currentUser = getCurrentAuthenticatedUser();

        Role roleToDelete = Role.builder()
                .deletedBy(currentUser.getId())
                .build();

        return roleManagementPort.deleteRole(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<Role> roles = roleManagementPort.getAllRoles();
        Set<UUID> userIds = roles.stream()
                .map(Role::getCreatedBy)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        userIds.addAll(roles.stream()
                .map(Role::getUpdatedBy)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        Map<UUID, String> userNames = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));

        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> toResponse(role, userNames))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return roleManagementPort.getRoleById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private User getCurrentAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("No active session found to audit the operation.");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email [" + email + "] does not exist in database. Access denied."));
    }

    private RoleResponse toResponse(Role role) {
        String createdByName = (role.getCreatedBy() != null)
                ? userRepository.findById(role.getCreatedBy()).map(User::getFullName).orElse(null)
                : null;

        String updatedByName = (role.getUpdatedBy() != null)
                ? userRepository.findById(role.getUpdatedBy()).map(User::getFullName).orElse(null)
                : null;

        return RoleResponse.fromDomain(role, createdByName, updatedByName);
    }

    private RoleResponse toResponse(Role role, Map<UUID, String> userNames) {
        String createdByName = userNames.get(role.getCreatedBy());
        String updatedByName = userNames.get(role.getUpdatedBy());
        return RoleResponse.fromDomain(role, createdByName, updatedByName);
    }
}
