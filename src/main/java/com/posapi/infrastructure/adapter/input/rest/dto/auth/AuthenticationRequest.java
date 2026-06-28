package com.posapi.infrastructure.adapter.input.rest.dto.auth;

public record AuthenticationRequest(String email, String password) {
}
