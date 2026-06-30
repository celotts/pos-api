package com.posapi.domain.model.user;

import com.posapi.domain.model.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    private String email;
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    public static User createNew(String email, String encodedPassword, String fullName, Role defaultRole, Boolean isActive, UUID createdBy) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .isActive(isActive)
                .failedLoginAttempts(0)
                .roleId(defaultRole.getId())
                .createdBy(createdBy)
                .build();
    }

    public User updateWith(User updateData, String newEncodedPassword, UUID newRoleId, UUID updatedBy) {
        return User.builder()
                .id(this.id)
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .roleId(newRoleId != null ? newRoleId : this.roleId)
                .failedLoginAttempts(this.failedLoginAttempts)
                .createdAt(this.createdAt)
                .createdBy(this.createdBy)
                .updatedBy(updatedBy)
                .deletedAt(this.deletedAt)
                .build();
    }
}
