package com.posapi.application.service.bootstrap;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.UserRepository;
import com.posapi.domain.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserManagementPort userManagementPort;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.roles.ADMIN:ADMIN}")
    private String adminRoleName;

    @Value("${app.roles.USER:USER}")
    private String userRoleName;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data bootstrap process...");
        createRoleIfNotFound(adminRoleName);
        createRoleIfNotFound(userRoleName);
        createOrUpdateAdminUser();
        log.info("Data bootstrap process finished.");
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            log.info("Role '{}' already exists. Skipping creation.", roleName);
            return;
        }
        log.info("Role '{}' not found. Creating...", roleName);
        Role newRole = Role.builder().id(UUID.randomUUID()).name(roleName).build();
        roleRepository.save(newRole);
        log.info("Role '{}' created successfully.", roleName);
    }

    private void createOrUpdateAdminUser() {
        Role adminRole = roleRepository.findByName(adminRoleName)
                .orElseThrow(() -> new IllegalStateException("Critical: ADMIN role not found after bootstrap attempt."));

        userRepository.findByEmail(adminEmail)
                .map(existingUser -> {
                    log.info("Admin user found. Ensuring password and role are up to date.");
                    boolean needsUpdate = false;
                    if (!passwordEncoder.matches(adminPassword, existingUser.getPassword())) {
                        existingUser.setPassword(passwordEncoder.encode(adminPassword));
                        needsUpdate = true;
                    }
                    if (!adminRole.getId().equals(existingUser.getRoleId())) {
                        existingUser.setRoleId(adminRole.getId());
                        existingUser.setRoleName(adminRole.getName());
                        needsUpdate = true;
                    }
                    if (needsUpdate) {
                        userRepository.save(existingUser);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    log.info("Admin user not found. Creating admin user with email '{}'...", adminEmail);
                    User adminTemplate = User.builder()
                            .email(adminEmail)
                            .password(adminPassword) // La contraseña se hashea en el servicio
                            .fullName("Default Administrator")
                            .isActive(true)
                            .roleId(adminRole.getId())
                            .roleName(adminRole.getName()) // 🛡️ SOLUCIÓN: Añadir el nombre del rol
                            .build();
                    return userManagementPort.createUser(adminTemplate);
                });
        log.info("Admin user is configured correctly.");
    }
}
