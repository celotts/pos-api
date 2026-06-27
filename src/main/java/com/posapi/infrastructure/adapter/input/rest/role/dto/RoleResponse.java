package com.posapi.infrastructure.adapter.input.rest.role.dto;

import com.posapi.domain.model.role.Role;

import java.util.UUID;

public record RoleResponse(UUID id, String name) {
    public static RoleResponse fromDomain(Role role) {
        return new RoleResponse(role.getId(), role.getName());
    }
}