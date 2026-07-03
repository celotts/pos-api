package com.posapi.infrastructure.adapter.input.rest.user.dto;

import com.posapi.domain.model.user.User;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String email,
    String fullName,
    String roleName,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt
) {
    public static UserResponse fromUser(User user, String roleName) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            roleName,
            user.getIsActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
