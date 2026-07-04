package com.posapi.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextHelper {

    public Optional<UserDetails> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        return (UserDetails) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    public Optional<String> getCurrentUsername() {
        return getCurrentUser().map(UserDetails::getUsername);
    }
}
