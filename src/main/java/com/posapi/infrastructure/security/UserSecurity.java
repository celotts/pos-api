package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.security.CustomUserDetails;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
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

        // World-Class Practice: Use a single, efficient method to get the authenticated user's ID.
        return getAuthenticatedUserId()
                .map(authenticatedUserId -> authenticatedUserId.equals(userId))
                .orElse(false);
    }

    public UUID getId() {
        // This implementation now uses the more efficient principal-first approach.
        return getAuthenticatedUserId().orElse(null);
    }

    private Optional<UUID> getAuthenticatedUserId() {
        // A single, fluent, and null-safe chain to get the user ID.
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .flatMap(principal -> {
                    // Efficient path: Get ID directly from our custom principal.
                    if (principal instanceof CustomUserDetails customUserDetails) {
                        return Optional.of(customUserDetails.getId());
                    }
                    // Fallback path: If principal is just a String (e.g., in some tests), look up by email.
                    if (principal instanceof String email && !"anonymousUser".equals(email)) {
                        return userRepository.findByEmail(email).map(User::getId);
                    }
                    return Optional.empty();
                });
    }
}
