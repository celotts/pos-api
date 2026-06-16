package com.posapi.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;

@Slf4j
@Component("customJwtRequestFilter")
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR_ATTRIBUTE = "expired";

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("JWT rejected: Token has expired");
                request.setAttribute(AUTH_ERROR_ATTRIBUTE,
                        "El token ha expirado. Por favor, inicie sesión nuevamente.");
            } catch (MalformedJwtException | SignatureException e) {
                log.warn("JWT rejected: Invalid signature or structure");
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, "El token es inválido o ha sido manipulado.");
            } catch (Exception e) {
                log.error("JWT rejected: Unexpected error during token parsing - {}", e.getMessage());
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, "Error interno procesando la autenticación.");
            }
        }

        authenticateRequest(request, username, jwt);
        chain.doFilter(request, response);
    }

    private void authenticateRequest(HttpServletRequest request, String username, String jwt) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Authenticating user: {}", username);

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
}