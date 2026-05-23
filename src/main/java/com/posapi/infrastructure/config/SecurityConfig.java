package com.posapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // Inyectar nuestro UserDetailsService

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para APIs REST
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated() // Requiere autenticación para todas las solicitudes
            )
            .httpBasic(withDefaults -> {}) // Habilita autenticación básica HTTP con la configuración por defecto
            .userDetailsService(userDetailsService); // Configura nuestro UserDetailsService personalizado
        return http.build();
    }

    // El bean userDetailsService(PasswordEncoder) que usaba InMemoryUserDetailsManager se elimina
    // Ahora se usará el UserDetailsServiceImpl inyectado

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
