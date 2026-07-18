package com.posapi.domain.port.output;

import com.posapi.domain.model.shift.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {
    Shift save(Shift shift);
    Optional<Shift> findById(UUID id);
    Page<Shift> findAll(Pageable pageable);
    List<Shift> findAll();
}
