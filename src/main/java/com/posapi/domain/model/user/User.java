package com.posapi.domain.model.user;

import com.posapi.domain.model.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Auditoría
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false)
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    // Campos de auditoría de rol añadidos
    @Column(name = "created_by_role_id")
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id")
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id")
    private UUID deletedByRoleId;

    // --- Métodos de UserDetails (Lógica de dominio de Seguridad) ---
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getName() == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    @Transient
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return isActive != null ? isActive : false;
    }

    // --- Creador semántico ---
    public static User createNew(
            String email,
            String encodedPassword,
            String fullName,
            String address,
            String phone,
            String phone2,
            Role role,
            Boolean isActive,
            UUID currentUserId,
            UUID currentUserRoleId
    ) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .address(address)
                .phone(phone)
                .phone2(phone2)
                .isActive(isActive)
                .role(role)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(currentUserRoleId)
                .build();
    }

    // --- Actualizador semántico ---
    public User updateWith(User updateData, String newEncodedPassword, Role newRole, UUID updatedByUserId, UUID updatedByRoleId) {
        return this.toBuilder()
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .address(updateData.getAddress() != null ? updateData.getAddress() : this.address)
                .phone(updateData.getPhone() != null ? updateData.getPhone() : this.phone)
                .phone2(updateData.getPhone2() != null ? updateData.getPhone2() : this.phone2)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .role(newRole != null ? newRole : this.role)
                .updatedAt(Instant.now())
                .updatedByUserId(updatedByUserId)
                .updatedByRoleId(updatedByRoleId)
                .build();
    }

    // Método de dominio para activar el usuario
    public void activate(UUID updatedByUserId, UUID updatedByRoleId) {
        if (!this.isActive) {
            this.isActive = true;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByRoleId = updatedByRoleId;
        }
    }

    // Método de dominio para desactivar el usuario
    public void deactivate(UUID updatedByUserId, UUID updatedByRoleId) {
        if (this.isActive) {
            this.isActive = false;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByRoleId = updatedByRoleId;
        }
    }

    // Método de dominio para cambiar la contraseña
    public void changePassword(String newEncodedPassword, UUID updatedByUserId, UUID updatedByRoleId) {
        if (newEncodedPassword == null || newEncodedPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        this.password = newEncodedPassword;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByRoleId = updatedByRoleId;
    }

    // Método de dominio para asignar un nuevo rol
    public void assignRole(Role newRole, UUID updatedByUserId, UUID updatedByRoleId) {
        if (newRole == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = newRole;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByRoleId = updatedByRoleId;
    }

    // Método de dominio para borrado lógico
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByRoleId = deletedByRoleId;
            this.isActive = false;
        }
    }
}
