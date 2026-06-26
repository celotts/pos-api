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

    /**
     * Factory method to create a new User with default values.
     * This ensures all new users are created in a consistent state.
     *
     * @param email The user's email.
     * @param encodedPassword The user's already-encoded password.
     * @param fullName The user's full name.
     * @param defaultRole The default role to assign to the new user.
     * @return A new, immutable User instance ready to be saved.
     */
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
                .build();
    }
    /**
     * Creates a new, updated User instance by merging this user's data
     * with the provided update information. This follows an immutable approach.
     *
     * @param updateData The partial user data to apply.
     * @param newEncodedPassword The new, already-encoded password, or null if not changing.
     * @param newRoleId The new, validated role ID, or null if not changing.
     * @return A new, immutable User instance with the updated data.
     */
    public User updateWith(User updateData, String newEncodedPassword, UUID newRoleId) {
        return User.builder()
                .id(this.id) // Unchanged
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .roleId(newRoleId != null ? newRoleId : this.roleId)
                .failedLoginAttempts(this.failedLoginAttempts) // Never updated by a user action
                .createdAt(this.createdAt) // Unchanged
                .updatedAt(Instant.now()) // Always updated on change
                .build();
    }


}