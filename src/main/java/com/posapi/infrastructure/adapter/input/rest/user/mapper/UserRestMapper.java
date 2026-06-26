package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.exception.ConfigurationException;
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
        String roleName = "UNKNOWN"; // Default value
        if (user.getRoleId() != null) {
            roleName = roleRepository.findById(user.getRoleId())
                    .map(Role::getName)
                    // Fallback in case the role was deleted from the DB but the user record still exists
                    .orElse(roleName);
        }

        return UserResponse.fromUser(user, roleName);
    }

    public User toDomain(UserRequest request, String roleName, boolean isActive) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ConfigurationException("Default role '" + roleName + "' not found in database."));

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