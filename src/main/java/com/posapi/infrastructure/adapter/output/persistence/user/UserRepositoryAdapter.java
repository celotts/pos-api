package com.posapi.infrastructure.adapter.output.persistence.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.user.UserPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByEmail(username).isPresent();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findByEmail(username);
    }

    @Override
    public User save(User user) {
        // Usa el mapeador para ir a entidad, evitando builders rotos aquí
        UserEntity entity = userPersistenceMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(User user) {
        if (user.getId() != null) {
            userJpaRepository.deleteById(user.getId());
        }
    }
}