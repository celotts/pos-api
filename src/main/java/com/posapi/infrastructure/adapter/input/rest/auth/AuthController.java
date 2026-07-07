package com.posapi.infrastructure.adapter.input.rest.auth;

import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.RefreshTokenRequest;
import com.posapi.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String username = jwtUtil.extractUsername(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(request.refreshToken(), userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            // Opcional: podrías generar un nuevo refresh token aquí también (rotación de tokens)
            return ResponseEntity.ok(new LoginResponse(newAccessToken, request.refreshToken()));
        }

        return ResponseEntity.badRequest().build();
    }
}
