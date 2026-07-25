package com.posapi.infrastructure.adapter.input.rest.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String token,
        UserResponse user
) {}
