package com.posapi.infrastructure.adapter.output.persistence.adapter.role;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.role.RolePersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.role.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public Role save(Role role) {
        RoleEntity roleEntity = rolePersistenceMapper.toEntity(role);
        return rolePersistenceMapper.toDomain(roleJpaRepository.save(roleEntity));
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findById(id)
                .map(rolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(rolePersistenceMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(rolePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleJpaRepository.findAll(pageable)
                .map(rolePersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        roleJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.existsByName(name);
    }

    @Override
    public long count() {
        return roleJpaRepository.count();
    }

    @Override
    public void delete(Role entity) {
        roleJpaRepository.delete(rolePersistenceMapper.toEntity(entity));
    }

    @Override
    public void deleteAll() {
        roleJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        roleJpaRepository.deleteAllById(ids);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleJpaRepository.existsById(id);
    }
}
