package com.posapi.infrastructure.adapter.input.rest.auth.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
