package com.posapi.infrastructure.adapter.output.persistence.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final Map<String, User> userStore = new HashMap<>();

    @Override
    public boolean existsByUsername(String username) {
        return userStore.containsKey(username);
    }
    @Override
    public boolean existsByEmail(String email) {
    for (User user : userStore.values()) {
        if (user.getEmail().equals(email)) {
            return true;
        }
    }
    return false;
}

    @Override
    public User save(User user) {
        userStore.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
    return userStore.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void delete(User user) {
        userStore.remove(user.getEmail());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userStore.values().stream()
                // Cambia .getUsername() por .getEmail()
                .filter(user -> user.getEmail().equals(username))
                .findFirst();
    }
}
