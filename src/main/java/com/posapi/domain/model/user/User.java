package com.posapi.domain.model.user;

import lombok.*;
import java.util.UUID;
import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String passwordHash;
    private String fullName;
    private String roleName;
    @Builder.Default
    private boolean isActive = true;
    private Instant createdAt;
    private Instant updatedAt;

    public String getRole() {
        return roleName != null ? roleName : "";
    }

    public String getUsername() {
        return fullName != null ? fullName : "";
    }

    public void setRole(String roleName) {
        this.roleName = roleName != null ? roleName : "";
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean b) {
        isActive = b;

    }
}