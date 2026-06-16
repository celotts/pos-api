package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userSecurity") // El nombre del bean debe coincidir con @userSecurity en @PreAuthorize
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isOwner(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String authenticatedUserEmail = authentication.getName(); // El email es el nombre de usuario en UserDetails

        return userRepository.findByEmail(authenticatedUserEmail)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}
