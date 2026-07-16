package com.posapi.domain.model.user;

import com.posapi.domain.model.role.Role; // Importar la entidad Role
import jakarta.persistence.*; // Importar todas las anotaciones de JPA
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
@Builder(toBuilder = true) // Añadido toBuilder para facilitar actualizaciones
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails { // Implementa UserDetails
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Asumiendo UUIDs auto-generados por la DB o Hibernate
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Relación Many-to-One con la entidad Role
    @ManyToOne(fetch = FetchType.EAGER) // EAGER fetch para roles es común para UserDetails
    @JoinColumn(name = "role_id", nullable = false) // Mapea a la columna role_id
    private Role role; // Cambiado de roleId a la entidad Role

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id") // Corregido para coincidir con SQL
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Corregido para coincidir con SQL
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // Corregido para coincidir con SQL
    private UUID deletedByUserId;

    @Column(name = "created_by_role_id") // Añadido para auditoría de rol
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id") // Añadido para auditoría de rol
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id") // Añadido para auditoría de rol
    private UUID deletedByRoleId;

    // --- Métodos de UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asumiendo que la entidad Role tiene un método getName() que devuelve el nombre del rol (ej. "ADMIN", "USER")
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    // getPassword() es generado por Lombok debido a @Data

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implementa lógica real si es necesario
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implementa lógica real si es necesario
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implementa lógica real si es necesario
    }

    @Override
    public boolean isEnabled() {
        return isActive != null ? isActive : false; // Usa el campo isActive
    }

    // --- Método estático para crear una nueva instancia de User ---
    public static User createNew(
            String email, String encodedPassword, String fullName, Role role, Boolean isActive, UUID currentUserId
    ) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .fullName(fullName)
                .isActive(isActive)
                .role(role) // Asignar el objeto Role directamente
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(role.getId()) // Asignar el ID del rol del creador
                .build();
    }

    // --- Método para actualizar una instancia de User ---
    public User updateWith(User updateData, String newEncodedPassword, Role newRole, UUID updatedByUserId) {
        return this.toBuilder() // Usar toBuilder para mantener los campos existentes
                .email(updateData.getEmail() != null ? updateData.getEmail() : this.email)
                .password(newEncodedPassword != null ? newEncodedPassword : this.password)
                .fullName(updateData.getFullName() != null ? updateData.getFullName() : this.fullName)
                .isActive(updateData.getIsActive() != null ? updateData.getIsActive() : this.isActive)
                .role(newRole != null ? newRole : this.role) // Asignar el nuevo objeto Role
                .updatedAt(Instant.now())
                .updatedByUserId(updatedByUserId)
                .updatedByRoleId(newRole != null ? newRole.getId() : this.role.getId()) // Asignar el ID del rol del actualizador
                .build();
    }
}
