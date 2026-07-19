package com.posapi.infrastructure.adapter.output.persistence.adapter.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.user.UserPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.user.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAllByFullName(String fullName) {
        return userJpaRepository.findAllByFullName(fullName).stream()
                .map(userPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        var entity = userPersistenceMapper.toEntity(user);
        var savedEntity = userJpaRepository.save(entity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByFullName(String fullName) {
        return userJpaRepository.existsByFullName(fullName);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return userJpaRepository.existsByRoleName(roleName);
    }

    // =============================================================================
    // IMPLEMENTACIÓN DE LOS MÉTODOS FALTANTES DE AUDITORÍA Y BÚSQUEDA GENERAL
    // =============================================================================

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAllById(Collection<UUID> ids) {
        return List.of();
    }

    @Override
    public List<User> findAllById(Iterable<UUID> ids) {
        return userJpaRepository.findAllById(ids).stream()
                .map(userPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
