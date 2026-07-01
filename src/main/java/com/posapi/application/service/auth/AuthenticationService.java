package com.posapi.application.service.auth;

import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.dto.auth.LoginRequest;
import com.posapi.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository; // Añadido para futuras mejoras si es necesario
    private final JwtUtil jwtUtil;

    public String login(@Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());

            return jwtUtil.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials for user: " + loginRequest.email());
        }
    }
}
