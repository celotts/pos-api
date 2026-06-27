package com.posapi.infrastructure.security;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Importamos explícitamente el User de Spring Security para mayor claridad
import org.springframework.security.core.userdetails.User.UserBuilder;

@Service("customUserDetailsService")
@RequiredArgsConstructor
// Elimina la necesidad de escribir el constructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        String roleName = getRoleName(user.getRoleId());

        // Usamos UserBuilder para mayor limpieza
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPassword());
        builder.authorities("ROLE_" + roleName);

        return builder.build();
    }

    // Movemos la lógica sucia a un método privado para que el flujo principal se vea limpio
    private String getRoleName(java.util.UUID roleId) {
        return roleRepository.findById(roleId)
                .map(Role::getName)
                .orElse("USER");
    }
}