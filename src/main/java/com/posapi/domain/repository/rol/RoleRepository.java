package com.posapi.domain.repository.rol;

import com.posapi.domain.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull; // Importa esta anotación
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    @NonNull
    Optional<Role> findById(@NonNull UUID id);

    @NonNull
    Optional<Role> findByName(@NonNull String name);
}