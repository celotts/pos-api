package com.posapi.infrastructure.security;

import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("customUserDetailsService") // Usamos @Component y mantenemos el nombre del bean por compatibilidad
@RequiredArgsConstructor
public class UserDetailsProvider implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // CORREGIDO: Acceder al nombre del rol directamente desde el objeto Role en la entidad User
        String roleName = user.getRole().getName();

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                // .authorities(roleName) // <--- QUITA ESTA LÍNEA (si aún está)
                .roles(roleName)         // <--- USA ESTA (Spring añadirá ROLE_ automáticamente)
                .build();
    }

    // ELIMINADO: Este método auxiliar ya no es necesario si accedemos al rol directamente desde el User
    // private String getRoleName(java.util.UUID roleId) {
    //     // Si el rol no se encuentra, se asigna 'USER' por defecto como medida de seguridad.
    //     return roleRepository.findById(roleId)
    //             .map(Role::getName)
    //             .orElse("USER");
    // }
}
