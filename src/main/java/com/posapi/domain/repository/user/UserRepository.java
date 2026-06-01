package com.posapi.domain.repository.user;

import com.posapi.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);

    List<User> findAll();

    void delete(User user);
    // Otros métodos de búsqueda o manipulación de usuarios que el dominio necesite
}
