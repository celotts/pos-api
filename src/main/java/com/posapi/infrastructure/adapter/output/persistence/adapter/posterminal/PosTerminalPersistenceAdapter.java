package com.posapi.infrastructure.adapter.output.persistence.adapter.posterminal;

import com.posapi.domain.model.posterminal.PosTerminal;
import com.posapi.domain.port.output.PosTerminalRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.posterminal.PosTerminalPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.posterminal.PosTerminalJpaRepository;
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
public class PosTerminalPersistenceAdapter implements PosTerminalRepository {

    private final PosTerminalJpaRepository posTerminalJpaRepository;
    private final PosTerminalPersistenceMapper posTerminalPersistenceMapper;

    @Override
    public PosTerminal save(PosTerminal posTerminal) {
        return posTerminalPersistenceMapper.toDomain(
                posTerminalJpaRepository.save(posTerminalPersistenceMapper.toEntity(posTerminal))
        );
    }

    @Override
    public Optional<PosTerminal> findById(UUID id) {
        return posTerminalJpaRepository.findById(id)
                .map(posTerminalPersistenceMapper::toDomain);
    }

    @Override
    public Page<PosTerminal> findAll(Pageable pageable) {
        return posTerminalJpaRepository.findAll(pageable)
                .map(posTerminalPersistenceMapper::toDomain);
    }

    @Override
    public List<PosTerminal> findAll() {
        return posTerminalJpaRepository.findAll().stream()
                .map(posTerminalPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PosTerminal> findByName(String name) {
        return posTerminalJpaRepository.findByName(name)
                .map(posTerminalPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return posTerminalJpaRepository.existsByName(name);
    }
}
