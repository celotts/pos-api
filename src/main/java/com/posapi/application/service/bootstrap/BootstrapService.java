package com.posapi.application.service.bootstrap;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootstrapService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.full-name:Admin User}")
    private String adminFullName;

    @Value("${app.bootstrap.admin.address:N/A}")
    private String adminAddress;

    @Value("${app.bootstrap.admin.phone:N/A}")
    private String adminPhone;

    @Value("${app.bootstrap.admin.phone2:N/A}")
    private String adminPhone2;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data bootstrap process...");
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");
        createOrUpdateAdminUser();
        log.info("Data bootstrap process finished.");
    }

    private void createRoleIfNotFound(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            log.info("Role '{}' not found. Creating...", roleName);
            Role newRole = Role.builder()
                    .name(roleName)
                    .createdAt(Instant.now())
                    .build();
            roleRepository.save(newRole);
            return;
        }
        log.info("Role '{}' already exists.", roleName);
    }

    private void createOrUpdateAdminUser() {
        Role adminRole = roleRepository
                .findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Admin role not found during bootstrap."
                        )
                );

        Optional<User> existingUser = userRepository.findByEmail(adminEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Actualizar si el rol ha cambiado
            if (!adminRole.getId().equals(user.getRole().getId())) {
                user.setRole(adminRole);
                log.info("Admin user role updated to 'ADMIN'.");
            }
            log.info("Admin user already exists.");
        } else {
            log.info("Admin user not found. Creating...");
            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName(adminFullName)
                    .address(adminAddress)
                    .phone(adminPhone)
                    .phone2(adminPhone2)
                    .isActive(true)
                    .role(adminRole)
                    .createdAt(Instant.now())
                    .build();
            userRepository.save(adminUser);
            log.info("Admin user created successfully.");
        }
    }
}
