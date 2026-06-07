package com.posapi.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;

@Getter // Genera getters por defecto
@RequiredArgsConstructor
public class User {
    private final String username;
    private final String email;

    @Getter(AccessLevel.NONE) // <--- ¡ESTO ES EL SECRETO!
    private final String password; // Nadie puede llamar a getPassword()
}