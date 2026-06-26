package com.posapi.domain.model.user;

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

    // ESTE ES EL CAMPO QUE TE FALTA
    private UUID roleId;

    private Instant createdAt;
    private Instant updatedAt;


}