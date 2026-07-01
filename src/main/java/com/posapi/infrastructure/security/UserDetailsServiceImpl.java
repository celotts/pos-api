package com.posapi.infrastructure.security;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User.UserBuilder;

@Service("customUserDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String roleName = getRoleName(user.getRoleId());

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        // 🛡️ CORRECCIÓN DEFINITIVA: Usar .roles() para que Spring añada el prefijo "ROLE_" automáticamente.
        // Esto hará que sea compatible con hasRole('ADMIN').
        builder.roles(roleName); 

        return builder.build();
    }

    private String getRoleName(java.util.UUID roleId) {
        return roleRepository.findById(roleId)
                .map(Role::getName)
                .orElse("USER");
    }
}
