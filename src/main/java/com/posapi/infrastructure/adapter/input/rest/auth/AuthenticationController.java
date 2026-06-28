package com.posapi.infrastructure.adapter.input.rest.auth;

import com.posapi.application.service.auth.AuthenticationService;
import com.posapi.infrastructure.adapter.input.rest.dto.auth.LoginRequest; // Import corregido
import com.posapi.infrastructure.adapter.input.rest.dto.auth.LoginResponse; // Import corregido
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticationService.login(loginRequest);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
