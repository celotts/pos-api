package com.posapi.infrastructure.config;

import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración global de seguridad para la API.
 * Utiliza autenticación JWT y es completamente stateless.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtRequestFilter requestFilter;
    private final AuthenticationConfiguration authConfiguration;

    public SecurityConfig(
            @Qualifier("customJwtAuthenticationEntryPoint") JwtAuthenticationEntryPoint entryPoint,
            @Qualifier("customJwtRequestFilter") JwtRequestFilter requestFilter,
            AuthenticationConfiguration authConfiguration) {
        this.entryPoint = entryPoint;
        this.requestFilter = requestFilter;
        this.authConfiguration = authConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitado para APIs REST
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Endpoints públicos (login, registro)
                        .anyRequest().hasRole("USER") // Todo lo demás requiere rol USER
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint) // Manejo de error 401
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No se crean sesiones
                )
                // Añadir el filtro de JWT antes del filtro estándar de autenticación
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}