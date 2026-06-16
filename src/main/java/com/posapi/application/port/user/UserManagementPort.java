package com.posapi.application.port.user;

import com.posapi.domain.model.user.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManagementPort {

    @NotNull
    User createUser(@NotNull User user);

    List<User> getAllUsers(); // Añadido

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByEmail(String email);

    Optional<User> updateUser(UUID id, @NotNull User user); // Añadido

    boolean deleteUser(UUID id); // Añadido
}
