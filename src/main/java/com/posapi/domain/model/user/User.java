package com.posapi.domain.model.user;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor // Necesario para frameworks de persistencia y serialización
@AllArgsConstructor // Genera el constructor PÚBLICO que el Builder y otras capas necesitan
public class User {
    @NonNull
    private UUID id;
    @NonNull
    private String email;
    @NonNull
    private String passwordHash;
    @NonNull
    private String fullName;
    private Boolean isActive;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;

}
