package com.posapi.application.service.bootstrap;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserManagementPort userManagementPort; // 🛡️ World-Class: Delegate to the use case port
    private final RoleRepository roleRepository;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.roles.ADMIN:ADMIN}")
    private String adminRoleName;

    @Value("${app.roles.USER:USER}")
    private String userRoleName;

    @Override
    public void run(String... args) {
        log.info("Starting data bootstrap process...");
        createRoleIfNotFound(adminRoleName);
        createRoleIfNotFound(userRoleName);
        createAdminUserIfNotFound();
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

    private void createAdminUserIfNotFound() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user with email '{}' already exists. Skipping creation.", adminEmail);
            return;
        }
        log.info("Admin user not found. Creating admin user with email '{}'...", adminEmail);

        Role adminRole = roleRepository.findByName(adminRoleName)
                .orElseThrow(() -> new IllegalStateException("Critical: ADMIN role not found after bootstrap attempt."));

        // 🛡️ World-Class: Delegate creation to the responsible service, don't replicate logic here.
        User adminTemplate = User.builder()
                .email(adminEmail)
                .password(adminPassword) // Pass raw password; the service will encode it.
                .fullName("Default Administrator")
                .isActive(true)
                .roleId(adminRole.getId())
                .build();

        userManagementPort.createUser(adminTemplate);
        log.info("Admin user created successfully.");
    }
}