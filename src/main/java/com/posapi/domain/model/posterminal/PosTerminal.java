package com.posapi.domain.model.posterminal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PosTerminal {
    private UUID id;
    private String name;
    private String location;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    public static PosTerminal createNew(String name, String location, UUID currentUserId, UUID currentUserRoleId) {
        return PosTerminal.builder()
                .id(java.util.UUID.randomUUID())
                .name(name)
                .location(location)
                .isActive(true)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    public void updateDetails(String newName, String newLocation, Boolean newIsActive, UUID updatedByUserId, UUID updatedByUserRoleId) {
        this.name = newName != null ? newName : this.name;
        this.location = newLocation != null ? newLocation : this.location;
        this.isActive = newIsActive != null ? newIsActive : this.isActive;
        this.updatedAt = java.time.Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
            this.isActive = false;
        }
    }
}
