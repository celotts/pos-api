package com.posapi.application.service.auth;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.dto.auth.LoginRequest;
import com.posapi.infrastructure.security.JwtUtil; // Import corregido
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
    private final UserDetailsService userDetailsService; // Usar UserDetailsService para obtener UserDetails
    private final JwtUtil jwtUtil; // Usar JwtUtil

    public String login(@Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Después de una autenticación exitosa, obtenemos los UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());

            // Generamos el token a partir de los UserDetails
            return jwtUtil.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials for user: " + loginRequest.email());
        }
    }
}
