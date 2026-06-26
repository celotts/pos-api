package com.posapi.infrastructure.adapter.output.persistence.adapter.role;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.role.RolePersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.role.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePersistenceMapper roleMapper;

    @Override
    public Role save(Role role) {
        var entity = roleMapper.toEntity(role);
        var savedEntity = roleJpaRepository.save(entity);
        return roleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findById(id).map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(roleMapper::toDomain)
                .collect(Collectors.toList());
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
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(roleMapper::toDomain);
    }
}