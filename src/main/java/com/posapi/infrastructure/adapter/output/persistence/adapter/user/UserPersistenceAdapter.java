package com.posapi.infrastructure.adapter.output.persistence.adapter.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.UserRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.user.UserPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        log.debug("Saving user with email: {}", user.getEmail());
        var entity = userMapper.toEntity(user);
        var savedEntity = userJpaRepository.save(entity);
        log.info("Successfully saved user with ID: {}", savedEntity.getId());
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.warn("Deleting user by ID: {}", id);
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking for existence of user by email: {}", email);
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(UUID id) {
        log.debug("Checking for existence of user by ID: {}", id);
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        log.debug("Checking for existence of user by role name: {}", roleName);
        return userJpaRepository.existsByRoleName(roleName);
    }

    @Override
    public void delete(User user) {

    }
}