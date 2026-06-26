package com.posapi.interfaces.rest.auth;

import com.posapi.application.service.auth.AuthenticationService;
import com.posapi.interfaces.rest.dto.auth.AuthenticationRequest;
import com.posapi.interfaces.rest.dto.auth.AuthenticationResponse;
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
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        String token = authenticationService.login(request.email(), request.password());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}