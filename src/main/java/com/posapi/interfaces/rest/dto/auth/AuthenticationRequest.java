package com.posapi.interfaces.rest.dto.auth;

public record AuthenticationRequest(String email, String password) {
}