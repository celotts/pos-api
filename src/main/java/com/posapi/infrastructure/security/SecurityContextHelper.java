package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {

    private final UserRepository userRepository;

    public Optional<UserDetails> getCurrentUserDetails() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        return (UserDetails) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    public Optional<String> getCurrentUsername() {
        return getCurrentUserDetails().map(UserDetails::getUsername);
    }

    /**
     * Obtiene el objeto de dominio User completo para el usuario autenticado.
     * Lanza una excepción si no se encuentra el usuario o la sesión.
     * @return El objeto User del dominio.
     */
    public User getCurrentUserOrThrow() {
        String email = getCurrentUsername()
                .orElseThrow(() -> new BadCredentialsException("No active session found to audit the operation."));
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email [" + email + "] does not exist in database. Access denied."));
    }
}
