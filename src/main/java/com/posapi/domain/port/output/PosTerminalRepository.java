package com.posapi.domain.port.output;

import com.posapi.domain.model.posterminal.PosTerminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PosTerminalRepository {
    PosTerminal save(PosTerminal posTerminal);
    Optional<PosTerminal> findById(UUID id);
    Page<PosTerminal> findAll(Pageable pageable);
    List<PosTerminal> findAll();
    Optional<PosTerminal> findByName(String name);
    boolean existsByName(String name);
}
