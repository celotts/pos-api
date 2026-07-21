package com.posapi.infrastructure.adapter.input.rest.user.mapper;

import com.posapi.domain.model.role.Role; // AÑADIDO
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest; // AÑADIDO
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.time.Instant; // AÑADIDO
import java.util.UUID;

@Component
public class UserRestMapper {

    // Método para mapear de UserRequest a User (dominio)
    public User toDomain(UserRequest request) {
        if (request == null) {
            return null;
        }
        // Aquí se crea un User parcial, solo con los campos del request.
        // Los campos de auditoría y el ID se manejan en el dominio o en el servicio.
        return User.builder()
                .email(request.email())
                .password(request.password())
                .fullName(request.fullName())
                .isActive(request.isActive() != null ? request.isActive() : true) // Asumir activo por defecto si no se especifica
                // El rol se asignará en el servicio, no aquí directamente desde el request
                .build();
    }

    public UserResponse toResponse(User user, String roleName, String createdByName, String updatedByName) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleName(roleName)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                // Los campos de auditoría de nombres (createdByName, updatedByName) se pasan directamente
                .build();
    }
}
