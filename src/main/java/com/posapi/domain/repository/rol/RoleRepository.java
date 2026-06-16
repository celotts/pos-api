package com.posapi.domain.repository.rol;

import com.posapi.domain.model.Role.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<@NonNull Role, @NonNull Long> {
    Role findByName(String name);
}