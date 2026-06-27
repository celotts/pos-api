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
    private UUID roleId;
    private String roleName;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted; // Campo añadido

    public static UserResponse fromUser(User user, String roleName) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .roleId(user.getRoleId())
                .roleName(roleName)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isDeleted(user.getDeletedAt() != null) // Mapear el estado de eliminación
                .build();
    }
}
