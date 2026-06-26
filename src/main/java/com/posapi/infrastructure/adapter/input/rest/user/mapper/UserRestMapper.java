package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRestMapper {

    private final RoleRepository roleRepository;

    public UserResponse toResponse(User user) {
        // The logic of finding the role name is now centralized here.
        String roleName = roleRepository.findById(user.getRoleId())
                .map(Role::getName)
                .orElse("UNKNOWN"); // Default value if role is not found

        return UserResponse.fromUser(user, roleName);
    }

    public User toDomain(UserRequest request, String roleName, boolean isActive) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));

        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .isActive(isActive)
                .roleId(role.getId())
                .failedLoginAttempts(0)
                .build();
    }
}