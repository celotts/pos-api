package com.posapi.application.service.auth;

import com.posapi.application.service.jwt.JwtService;
import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Validated
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String login(@Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 1. Extraer los roles/autoridades
            Map<String, Object> extraClaims = new HashMap<>();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority != null ? authority.getAuthority() : "")
                    .filter(role -> !role.isEmpty())
                    .toList();

            // 2. Guardar los roles en el token bajo la clave "roles"
            extraClaims.put("roles", roles);

            // 3. Pasar los claims al generador
            return jwtService.generateToken(extraClaims, userDetails);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
