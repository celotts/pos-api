package com.posapi.domain.model.user;

import com.posapi.domain.model.role.Role;
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
public class User {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private Boolean isActive;
    private Integer failedLoginAttempts;

    private UUID roleId;
    private String roleName;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt; // Campo añadido

    public static User createNew(String email, String encodedPassword, String fullName, Role defaultRole) {
        Instant now = Instant.now();
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .isActive(true)
                .failedLoginAttempts(0)
                .roleId(defaultRole.getId())
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null) // explícitamente nulo al crear
                .build();
    }

    public User updateWith(User updateData, String newEncodedPassword, UUID newRoleId) {
        return User.builder()
                .id(this.id)
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .roleId(newRoleId != null ? newRoleId : this.roleId)
                .failedLoginAttempts(this.failedLoginAttempts)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .deletedAt(this.deletedAt) // mantener el estado de eliminación
                .build();
    }
}
