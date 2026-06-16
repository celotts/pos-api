package com.posapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    @SuppressWarnings("null")
    public AuditorAware<String> auditorProvider() {
        return () -> {
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .filter(Authentication::isAuthenticated)
                    .filter(auth -> !"anonymousUser".equals(auth.getPrincipal()))
                    .map(Authentication::getName)
                    .or(() -> Optional.of("SYSTEM"));
        };
    }
}