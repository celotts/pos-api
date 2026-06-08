package com.posapi.domain.repository.user;

import com.posapi.domain.model.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // Métodos principales
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(User user);

    // Validaciones y búsquedas adicionales
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}