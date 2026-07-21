package com.posapi.domain.port.output;

import com.posapi.domain.model.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository { // Interfaz de dominio limpia, NO EXTIENDE JpaRepository

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByName(String name);

    List<Role> findAll(); // Obtener todos sin paginación

    Page<Role> findAll(Pageable pageable); // Obtener todos paginados

    void deleteById(UUID id);

    boolean existsByName(String name);

    long count(); // AÑADIDO
    void delete(Role entity); // AÑADIDO
    void deleteAll(); // AÑADIDO
    void deleteAllById(Iterable<? extends UUID> ids); // AÑADIDO
    boolean existsById(UUID id); // AÑADIDO

    // NO SE INCLUYEN MÉTODOS DE JpaRepository como saveAll, flush, deleteAll(Iterable),
    // getOne, findOne, findAll(Example), etc., porque son detalles de infraestructura.
    // El dominio no necesita saber de ellos.
}
