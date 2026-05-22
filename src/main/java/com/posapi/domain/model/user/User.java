package com.posapi.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull; // Importar NonNull

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NonNull
    private UUID id;
    @NonNull
    private String email;
    @NonNull
    private String passwordHash; // Asumimos que la contraseña siempre estará presente
    @NonNull
    private String fullName;
    private Boolean isActive; // Puede ser null si no se inicializa, pero en UserService lo forzamos a true
    @NonNull
    private String role; // Asumimos que el rol siempre estará presente
    @NonNull
    private Instant createdAt;
    @NonNull
    private Instant updatedAt;
    private Instant deletedAt; // Puede ser null
    private UUID createdByUserId; // Puede ser null
    private UUID updatedByUserId; // Puede ser null
    private UUID deletedByUserId; // Puede ser null

    // Métodos de dominio específicos para User podrían ir aquí
    // Por ejemplo, para verificar la contraseña, asignar roles, etc.
}
