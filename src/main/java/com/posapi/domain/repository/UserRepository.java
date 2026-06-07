package com.posapi.domain.repository;

import com.posapi.domain.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(User user);

    User findByUsername(String username);

    User findByEmail(String email);
}