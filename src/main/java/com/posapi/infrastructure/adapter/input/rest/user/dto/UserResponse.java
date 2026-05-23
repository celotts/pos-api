package com.posapi.infrastructure.adapter.input.rest.user.dto;

import com.posapi.domain.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull; // Importar NotNull de Jakarta Validation
// Mantener para los campos si se desea, o cambiar a NotNull también

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @NotNull
    private UUID id;
    private String email;
    @NotNull
    private String fullName;
    private boolean isActive;
    @NotNull
    private String role;
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;

    @NotNull
    public static UserResponse fromUser(User user) {
        if (user == null) {
            // Devolver un UserResponse con valores por defecto no nulos
            return UserResponse.builder()
                    .id(UUID.randomUUID())
                    .email("")
                    .fullName("")
                    .isActive(false)
                    .role("")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
        }

        // Aseguramos que isActive siempre sea un boolean primitivo
        boolean activeStatus = (user.getIsActive() != null) ? user.getIsActive() : false;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(activeStatus)
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
