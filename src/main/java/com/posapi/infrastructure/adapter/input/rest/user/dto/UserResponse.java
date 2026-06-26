package com.posapi.infrastructure.adapter.input.rest.user.dto;

import com.posapi.domain.model.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private boolean isActive;
    private UUID roleId; // 👈 CAMBIO: Campo añadido para el ID del rol
    private String roleName;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse fromUser(User user, String roleName) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .roleId(user.getRoleId()) // 👈 CAMBIO: Mapear el ID del rol
                .roleName(roleName)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}