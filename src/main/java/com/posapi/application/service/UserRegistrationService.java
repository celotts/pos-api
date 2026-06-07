package com.posapi.application.service;

import com.posapi.application.payload.UserRegistrationRequest;
import com.posapi.domain.model.User;
import com.posapi.domain.repository.UserRepository;
import jakarta.validation.ValidationException;

public final class UserRegistrationService {
    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        userRepository.save(user);
    }
}