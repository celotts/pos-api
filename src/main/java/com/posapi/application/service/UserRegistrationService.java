package com.posapi.application.service;

import com.posapi.application.payload.UserRegistrationRequest;
import com.posapi.domain.model.User;
import com.posapi.domain.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor; // Importa esto
import org.springframework.stereotype.Service;

@Service // Marca esto como un servicio de Spring
@RequiredArgsConstructor // Esto genera el constructor por ti automáticamente
public final class UserRegistrationService {

    private final UserRepository userRepository;

    public void registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ValidationException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email already exists");
        }

        User user = new User(request.username(), request.email(), request.password());
        userRepository.save(user);
    }
}