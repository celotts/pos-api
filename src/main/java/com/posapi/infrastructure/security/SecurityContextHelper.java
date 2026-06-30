package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

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
