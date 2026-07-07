package com.posapi.infrastructure.adapter.output.persistence.adapter.user;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.user.UserPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPersistenceAdapter {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userMapper;

    @Transactional
    public User save(User user) {
        log.debug("Saving user with email: {}", user.getEmail());
        UserEntity entity = userMapper.toEntity(user);

        // 1. Guardar y forzar la escritura en la base de datos
        UserEntity savedEntity = userJpaRepository.saveAndFlush(entity);
        log.info("Successfully flushed user with ID: {}", savedEntity.getId());

        // 2. Mapear la entidad devuelta (que ahora tiene los timestamps) al dominio
        return userMapper.toDomain(savedEntity);
    }
}
