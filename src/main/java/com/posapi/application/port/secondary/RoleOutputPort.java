package com.posapi.application.port.secondary;

import com.posapi.domain.model.role.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleOutputPort {
    Role save(Role role);
    Optional<Role> findById(UUID id);
    List<Role> findAll();
    void deleteById(UUID id);
    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
