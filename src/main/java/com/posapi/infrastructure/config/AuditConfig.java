package com.posapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.posapi.infrastructure.security.UserSecurity;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            // 🛡️ World-Class Practice: Return the User's UUID for type-safe auditing relationships.
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(Authentication.class::cast)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal)
                    .map(principal -> {
                        if (principal instanceof UserSecurity userSecurity) {
                            return userSecurity.getId();
                        }
                        // Handle system processes or anonymous users
                        return null;
                    });
        };
    }
}