package com.posapi.infrastructure.adapter.input.rest.user.dto;

import com.posapi.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Boolean isActive;
    private String roleName;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse fromUser(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive()) // 👈 Hace match con el Boolean wrapper de tu modelo
                .roleName(user.getRoleName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}