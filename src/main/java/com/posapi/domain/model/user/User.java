package com.posapi.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
public class User {
    @NonNull
    private UUID id;
    @NonNull
    private String email;
    @NonNull
    private String passwordHash;
    @NonNull
    private String fullName;
    private Boolean isActive;
    @NonNull
    private String role;
    @NonNull
    private Instant createdAt;
    @NonNull
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;

    public void setPasswordHash(String passwordHash, PasswordEncoder passwordEncoder) {
        this.passwordHash = Objects.requireNonNull(passwordEncoder.encode(passwordHash));
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = Objects.requireNonNull(deletedAt);
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = Objects.requireNonNull(createdByUserId);
    }

    public void setUpdatedByUserId(UUID updatedByUserId) {
        this.updatedByUserId = Objects.requireNonNull(updatedByUserId);
    }

    public void setDeletedByUserId(UUID deletedByUserId) {
        this.deletedByUserId = Objects.requireNonNull(deletedByUserId);
    }

}
