package com.posapi.domain.model.user;

import com.posapi.domain.model.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private Boolean isActive;
    private Role role;

    // Auditoría
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByRoleId;
    private UUID updatedByRoleId;
    private UUID deletedByRoleId;

    // --- Métodos de UserDetails (Lógica de dominio de Seguridad) ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getName() == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive != null ? isActive : false;
    }

    // --- Creador semántico ---
    public static User createNew(
            String email, String encodedPassword, String fullName, Role role, Boolean isActive, UUID currentUserId
    ) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .isActive(isActive)
                .role(role)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(role != null ? role.getId() : null)
                .build();
    }

    // --- Actualizador semántico ---
    public User updateWith(User updateData, String newEncodedPassword, Role newRole, UUID updatedByUserId) {
        return this.toBuilder()
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .role(newRole != null ? newRole : this.role)
                .updatedAt(Instant.now())
                .updatedByUserId(updatedByUserId)
                .updatedByRoleId(newRole != null ? newRole.getId() : (this.role != null ? this.role.getId() : null))
                .build();
    }
}
