package com.posapi.infrastructure.adapter.input.rest.role.dto;

import com.posapi.domain.model.role.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RoleResponse {
    private UUID id;
    private String name;

    public static RoleResponse fromDomain(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}