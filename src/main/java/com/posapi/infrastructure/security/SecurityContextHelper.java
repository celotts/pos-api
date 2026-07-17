package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {

    private final UserRepository userRepository;

    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }

    public Optional<User> getCurrentUser() {
        return getCurrentUsername().flatMap(userRepository::findByEmail);
    }

    public User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new SecurityException("No authenticated user found in security context."));
    }

    // AÑADIDO: Método para obtener el ID del usuario actual
    public UUID getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId)
                .orElseThrow(() -> new SecurityException("Authenticated user ID not found."));
    }

    // AÑADIDO: Método para obtener el ID del rol del usuario actual
    public UUID getCurrentUserRoleId() {
        return getCurrentUser()
                .map(User::getRole)
                .map(com.posapi.domain.model.role.Role::getId) // Acceder al ID del rol desde el objeto Role
                .orElseThrow(() -> new SecurityException("Authenticated user role ID not found."));
    }
}
