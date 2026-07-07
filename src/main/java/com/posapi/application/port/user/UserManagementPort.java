package com.posapi.application.port.user;

import com.posapi.domain.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManagementPort {
    User createUser(User user);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    Optional<User> updateUser(UUID id, User user);
    boolean deleteUser(UUID id);
}
