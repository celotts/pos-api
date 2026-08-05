package com.posapi.infrastructure.adapter.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponse {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
}
