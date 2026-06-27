package com.posapi.interfaces.rest.dto.auth;

public record LoginRequest(String email, String password) {
}