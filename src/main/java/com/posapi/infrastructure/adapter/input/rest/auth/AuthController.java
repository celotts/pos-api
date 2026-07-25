package com.posapi.infrastructure.adapter.input.rest.auth;

import com.posapi.application.port.auth.AuthManagementPort;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthManagementPort authManagementPort;

    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authManagementPort.authenticate(request);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
