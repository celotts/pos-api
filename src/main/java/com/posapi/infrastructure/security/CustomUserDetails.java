package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final boolean enabled;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.getIsActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora devolvemos una lista vacía, ajústalo según tus roles
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Spring usa email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta nunca expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta nunca se bloquea (a menos que añadas lógica extra)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // La contraseña nunca vence
    }

    @Override
    public boolean isEnabled() {
        return this.enabled; // Aquí sí usamos el estado real de tu entidad User
    }
}
