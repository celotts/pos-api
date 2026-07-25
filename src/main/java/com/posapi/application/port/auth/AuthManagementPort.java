package com.posapi.application.port.auth;

import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;

public interface AuthManagementPort {
    LoginResponse authenticate(LoginRequest request);
}
