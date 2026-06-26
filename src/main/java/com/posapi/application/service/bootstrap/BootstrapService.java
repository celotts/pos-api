package com.posapi.application.service.bootstrap;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    private static final String ADMIN_ROLE_NAME = "ADMIN";

    @Override
    public void run(String... args) {
        log.info("Verificando existencia del admin...");

        if (userRepository.existsByRoleName(ADMIN_ROLE_NAME)) {
            return;
        }

        // Aquí usamos el puerto de rol que sí configuramos en PersistenceConfig
        Role adminRole = roleRepository.findByName(ADMIN_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no existe"));

        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .fullName("Default Administrator")
                .isActive(true)
                .roleId(adminRole.getId())
                .failedLoginAttempts(0)
                .build();

        userRepository.save(adminUser);
    }
}