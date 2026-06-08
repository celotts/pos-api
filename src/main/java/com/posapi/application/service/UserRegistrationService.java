package com.posapi.application.service;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Marca esto como un servicio de Spring
public final class UserRegistrationService {

    @Autowired
    // Inyecta la dependencia automáticamente
    private UserRepository userRepository;

    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        userRepository.save(user);
    }
}

    // Otros métodos...
