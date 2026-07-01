package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRestMapper {

    private final RoleRepository roleRepository;

    public UserResponse toResponse(User user) {
        String roleName = roleRepository.findById(user.getRoleId())
                .map(Role::getName)
                .orElse("UNKNOWN");

        return UserResponse.fromUser(user, roleName);
    }

    public User toDomain(UserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .roleId(request.getRoleId())
                .isActive(request.isActive())
                .build();
    }
}
