package com.posapi.application.port.user;

import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManagementPort {

    UserResponse createUser(UserRequest userRequest);

    Optional<UserResponse> getUserById(UUID id);

    Optional<UserResponse> getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    Optional<UserResponse> updateUser(UUID id, UserRequest userRequest);

    void deleteUser(UUID id); // CORREGIDO: Retorna void
}
