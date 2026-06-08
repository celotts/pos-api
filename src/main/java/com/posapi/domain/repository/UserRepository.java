package com.posapi.domain.repository;

import com.posapi.domain.model.User;

// SIN ANOTACIONES AQUÍ
public interface UserRepository {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
}