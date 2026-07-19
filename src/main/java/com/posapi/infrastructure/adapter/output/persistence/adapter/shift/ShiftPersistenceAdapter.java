package com.posapi.infrastructure.adapter.output.persistence.adapter.shift;

import com.posapi.domain.model.shift.Shift;
import com.posapi.domain.port.output.ShiftRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.shift.ShiftPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.shift.ShiftJpaRepository;
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
public class ShiftPersistenceAdapter implements ShiftRepository {

    private final ShiftJpaRepository shiftJpaRepository;
    private final ShiftPersistenceMapper shiftPersistenceMapper;

    @Override
    public Shift save(Shift shift) {
        return shiftPersistenceMapper.toDomain(
                shiftJpaRepository.save(shiftPersistenceMapper.toEntity(shift))
        );
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return shiftJpaRepository.findById(id)
                .map(shiftPersistenceMapper::toDomain);
    }

    @Override
    public Page<Shift> findAll(Pageable pageable) {
        return shiftJpaRepository.findAll(pageable)
                .map(shiftPersistenceMapper::toDomain);
    }

    @Override
    public List<Shift> findAll() {
        return shiftJpaRepository.findAll().stream()
                .map(shiftPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
