package com.posapi.infrastructure.adapter.input.rest.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleManagementPort roleManagementPort;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Role roleToCreate = Role.builder().name(request.name()).createdBy(currentUser.getId()).build();
        Role createdRole = roleManagementPort.createRole(roleToCreate);
        return new ResponseEntity<>(
                toResponse(createdRole, Map.of(currentUser.getId(), currentUser.getFullName())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Role roleToUpdate = Role.builder().name(request.name()).updatedBy(currentUser.getId()).build();
        return roleManagementPort.updateRole(id, roleToUpdate)
                .map(updatedRole -> {
                    Set<UUID> userIds = Stream.of(updatedRole.getCreatedBy(), updatedRole.getUpdatedBy())
                            .filter(Objects::nonNull).collect(Collectors.toSet());
                    return toResponse(updatedRole, fetchUserNames(userIds));
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<Role> roles = roleManagementPort.getAllRoles();
        Set<UUID> userIds = roles.stream()
                .flatMap(role -> Stream.of(role.getCreatedBy(), role.getUpdatedBy()))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> toResponse(role, userNames))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleResponses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return roleManagementPort.getRoleById(id)
                .map(role -> {
                    Set<UUID> userIds = Stream.of(role.getCreatedBy(), role.getUpdatedBy())
                            .filter(Objects::nonNull).collect(Collectors.toSet());
                    return toResponse(role, fetchUserNames(userIds));
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleManagementPort.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private RoleResponse toResponse(Role role, Map<UUID, String> userNames) {
        String createdByName = role.getCreatedBy() != null ? userNames.get(role.getCreatedBy()) : null;
        String updatedByName = role.getUpdatedBy() != null ? userNames.get(role.getUpdatedBy()) : null;
        return RoleResponse.fromDomain(role, createdByName, updatedByName);
    }
}
