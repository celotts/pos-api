package com.posapi.interfaces.rest.auth;

import com.posapi.application.service.auth.AuthenticationService;
import com.posapi.interfaces.rest.dto.auth.AuthenticationResponse;
import com.posapi.interfaces.rest.dto.auth.LoginRequest;
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
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        String token = authenticationService.login(request.email(), request.password());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
// Records removed from here, utilizing the imported ones instead