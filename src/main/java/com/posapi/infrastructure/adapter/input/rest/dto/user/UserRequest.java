package com.posapi.infrastructure.adapter.input.rest.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserRequest {
    private String email;
    private String password;
    private String fullName;
    private UUID roleId;
    private boolean isActive;
}
