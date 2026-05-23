package com.posapi.application.port.user;

import com.posapi.domain.model.user.User;
import jakarta.validation.constraints.NotNull; // Importar NotNull

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManagementPort {

    @NotNull // Indica que este método siempre devuelve un User no nulo
    User createUser(@NotNull User user); // También marcamos el parámetro como NotNull
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    // Puedes añadir métodos para actualizar usuario, cambiar contraseña, etc.
}
