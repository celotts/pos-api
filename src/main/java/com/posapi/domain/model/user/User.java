package com.posapi.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.UUID;

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
    private Boolean isActive;
    private String role; // Representará el ENUM user_role como String en el dominio
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;

    // Métodos de dominio específicos para User podrían ir aquí
    // Por ejemplo, para verificar la contraseña, asignar roles, etc.
}
