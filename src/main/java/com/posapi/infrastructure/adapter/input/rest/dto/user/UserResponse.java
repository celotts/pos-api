package com.posapi.infrastructure.adapter.input.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    // --- Standard Fields ---
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    // --- User-Specific Fields ---
    private String email;
    private String fullName;
    private UUID roleId;
    private String roleName;
    private boolean active;
    private boolean deleted;

    public static UserResponse fromUser(User user, String roleName) {
        boolean isDeleted = user.getDeletedAt() != null;
        return UserResponse.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleId(user.getRoleId())
                .roleName(roleName)
                .active(user.getIsActive() && !isDeleted)
                .deleted(isDeleted)
                .build();
    }
}
