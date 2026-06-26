package com.posapi.domain.repository;

import com.posapi.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    // More efficient to delete by ID, avoids a SELECT before DELETE.
    void deleteById(UUID id);

    boolean existsByEmail(String email);
    boolean existsById(UUID id);

    boolean existsByRoleName(String roleName);
}