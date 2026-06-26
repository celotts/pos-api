package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
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

        // Assumes UserResponse has a fromUser(User, String) static method
        return UserResponse.fromUser(user, roleName);
    }
}