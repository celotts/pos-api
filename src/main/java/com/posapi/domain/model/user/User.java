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
    private Instant deletedAt;

    // 🛡️ CORRECCIÓN: Dejar que la capa de persistencia maneje los timestamps
    public static User createNew(String email, String encodedPassword, String fullName, Role defaultRole) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .isActive(true)
                .failedLoginAttempts(0)
                .roleId(defaultRole.getId())
                .deletedAt(null)
                // createdAt y updatedAt serán establecidos por JPA
                .build();
    }

    // 🛡️ CORRECCIÓN: Dejar que la capa de persistencia maneje el updatedAt
    public User updateWith(User updateData, String newEncodedPassword, UUID newRoleId) {
        return User.builder()
                .id(this.id)
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .roleId(newRoleId != null ? newRoleId : this.roleId)
                .failedLoginAttempts(this.failedLoginAttempts)
                .createdAt(this.createdAt) // Mantener el original
                .deletedAt(this.deletedAt)
                // updatedAt será establecido por JPA
                .build();
    }
}
