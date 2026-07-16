package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.shared.exception.ResourceNotFoundException; // Añadido para manejar rol no encontrado
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRestMapper {

    private final RoleRepository roleRepository;

    public UserResponse toResponse(User user) {
        // CORREGIDO: Acceder al nombre del rol a través del objeto Role
        String roleName = user.getRole().getName();

        return UserResponse.fromUser(user, roleName);
    }

    public User toDomain(UserRequest request) {
        // CORREGIDO: Obtener el objeto Role completo antes de construir el User
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID " + request.roleId() + " not found."));

        return User.builder()
                .email(request.email())
                .password(request.password())
                .fullName(request.fullName())
                .role(role) // CORREGIDO: Asignar el objeto Role
                .isActive(request.isActive())
                .build();
    }
}
